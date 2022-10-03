package com.alexeykovzel.fi.core.trade.form4;

import com.alexeykovzel.fi.core.insider.InsiderRepository;
import com.alexeykovzel.fi.core.stock.StockRepository;
import com.alexeykovzel.fi.core.insider.Insider;
import com.alexeykovzel.fi.core.EdgarService;
import com.alexeykovzel.fi.core.stock.Stock;
import com.alexeykovzel.fi.core.trade.Trade;
import com.alexeykovzel.fi.core.trade.TradeRepository;
import com.alexeykovzel.fi.utils.DateUtils;
import com.alexeykovzel.fi.utils.ProgressBar;
import com.alexeykovzel.fi.utils.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Form4Service extends EdgarService {
    private final StockRepository stockRepository;
    private final Form4Repository form4Repository;
    private final InsiderRepository insiderRepository;
    private final TradeRepository tradeRepository;

    public void updateFilings(String... symbols) {
        for (String symbol : symbols) {
            updateFilings(stockRepository.findBySymbol(symbol));
        }
    }

    public void updateFilings(Stock stock) {
        String message = String.format("Updating %s filings...", stock.getSymbol());
        updateFilings(getForm4Filings(stock), message);
    }

    public void updateFilings(int year, int quarter) {
        String message = String.format("Updating Y%d Q%d filings...", year, quarter);
        updateFilings(getForm4Filings(year, quarter), message);
    }

    public void updateRecentFilings(int from, int to) {
        String message = String.format("Updating recent %d-%d filings...", from, to);
        updateFilings(getRecentForm4Filings(from, to), message);
    }

    public void updateFilingsDaysAgo(int days) {
        String message = String.format("Updating %d-day filings...", days);
        updateFilings(getForm4Filings(days), message);
    }

    public void updateFilingsSecondsAgo(int seconds) {
        Date lastDate = DateUtils.shiftSeconds(new Date(), -seconds);
        String message = String.format("Updating %d-second filings...", seconds);
        updateFilings(getForm4Filings(0).stream()
                .filter(form4 -> form4.getDate().after(lastDate))
                .collect(Collectors.toSet()), message);
    }

    @Transactional
    public void updateFilings(Collection<Form4> form4s, String message) {
        Collection<String> existingFilings = form4Repository.findAllAccessionNumbers();
        Form4Parser form4Parser = new Form4Parser();
        ProgressBar.execute(message, form4s, form4 -> {
            try {
                // skip if such filing already exists
                if (existingFilings.contains(form4.getAccessionNo())) return;
                // otherwise, request filing data
                JsonNode root = getFilingData(form4.getUrl());
                Stock stock = getAndSaveStock(root);
                // update reporting insiders
                Collection<Insider> insiders = form4Parser.getReportingInsiders(root);
                insiders.forEach(insider -> insider.setStock(stock));
                // save form 4 data (incl. trades)
                Collection<Trade> trades = form4Parser.getTrades(root);
                trades.forEach(trade -> trade.setForm4(form4));
                form4.setStock(stock);
                form4.setInsiders(insiders);
                form4.setTrades(trades);
                form4Repository.save(form4);
            } catch (IOException e) {
                System.out.println("[ERROR] Failed to update filing: " + e.getMessage());
            } catch (NullPointerException e) {
                System.out.println("[ERROR] Invalid filing: " + form4.getUrl() + "\n");
            }
        });
    }

    private Stock getAndSaveStock(JsonNode root) {
        Form4Parser form4Parser = new Form4Parser();
        String cik = form4Parser.getIssuerCik(root);
        // save stock if it wasn't found
        if (!stockRepository.existsById(cik)) {
            Stock stock = form4Parser.getFilingIssuer(root);
            stockRepository.save(stock);
        }
        // return reference to the stock
        return stockRepository.getReferenceById(cik);
    }

    public Collection<Form4> getRecentForm4Filings(int from, int to) {
        Collection<Form4> form4s = new HashSet<>();
        try {
            String feedUrl = String.format(FORM4_RECENT_URL, from, to);
            String feed = getTextByUrl(feedUrl);
            feed = feed.substring(feed.indexOf("\n") + 1);
            for (JsonNode entry : new XmlMapper().readTree(feed).get("entry")) {
                // skip if filing is not of form 4
                if (isNotForm4(entry.get("category").get("term").asText())) continue;
                // retrieve filing accession no
                String id = entry.get("id").asText();
                String accessionNo = id.substring(id.indexOf("=") + 1);
                // receive filing date
                String dateVal = entry.get("updated").asText();
                dateVal = dateVal.substring(0, dateVal.indexOf("T"));
                Date date = DateUtils.parseEdgar(dateVal);
                // retrieve filing url
                String cik = entry.get("link").get("href").asText().split("/")[6];
                String url = String.format(FORM4_URL, cik, accessionNo);
                // save filing data
                form4s.add(new Form4(accessionNo, date, url));
            }
        } catch (IOException e) {
            System.out.printf("[ERROR] Getting recent %d-%d filings: %s\n", from, to, e.getMessage());
        }
        return form4s;
    }

    private Collection<Form4> getForm4Filings(Stock stock) {
        Collection<Form4> form4s = new HashSet<>();
        try {
            // define nodes for accessing filing data
            String cik = stock.getCik();
            JsonNode root = getJsonByUrl(String.format(SUBMISSIONS_URL, "CIK" + cik));
            JsonNode recent = root.get("filings").get("recent");
            JsonNode accessions = recent.get("accessionNumber");
            JsonNode forms = recent.get("form");
            JsonNode dates = recent.get("filingDate");
            for (int i = 0; i < accessions.size(); i++) {
                // skip non-insider trades (not of 4-th form)
                if (!forms.get(i).asText().equals("4")) continue;
                // retrieve filing data and add it to the list
                Date date = DateUtils.parseEdgar(dates.get(i).asText());
                String accessionNo = accessions.get(i).asText();
                String url = String.format(FORM4_URL, StringUtils.trimLeadingZeros(cik), accessionNo);
                form4s.add(new Form4(accessionNo, date, url));
            }
        } catch (IOException e) {
            System.out.printf("[ERROR] Getting filings of %s stock: %s\n", stock.getSymbol(), e.getMessage());
        }
        return form4s;
    }

    private Collection<Form4> getForm4Filings(int year, int quarter) {
        Collection<Form4> form4s = new HashSet<>();
        Collection<String> takenFilings = new HashSet<>();
        try (InputStream in = getInputStreamByUrl(String.format(FULL_INDEX_URL, year, quarter), "text/html");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] args = line.split("\\|");
                // check if contains all the fields
                if (args.length != 5) continue;
                // check if filing is of the 4-th form
                if (isNotForm4(args[2])) continue;
                // retrieve filing data
                String accessionNo = args[4]
                        .substring(args[4].lastIndexOf("/") + 1)
                        .replace(".txt", "");
                if (takenFilings.contains(accessionNo)) continue;
                Date date = DateUtils.parseEdgar(args[3]);
                String url = SEC_URL + "/Archives/" + args[4];
                form4s.add(new Form4(accessionNo, date, url));
                takenFilings.add(accessionNo);
            }
        } catch (IOException e) {
            System.out.printf("[ERROR] Getting filings of %d year %d quarter: %s\n", year, quarter, e.getMessage());
        }
        return form4s;
    }

    private Collection<Form4> getForm4Filings(int daysAgo) {
        Collection<Form4> form4s = new HashSet<>();
        try (InputStream in = getInputStreamByUrl(String.format(FORM4_DAYS_AGO_URL, daysAgo), "text/html");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // retrieve a filing reference
                String[] parts = line.split("\"");
                // skip if a line does not contain 2 hrefs
                if (parts.length < 2 || !parts[1].startsWith("/Archives")) continue;
                // skip if a filing is not of form 4
                String code = parts[2].substring(1, parts[2].indexOf("<"));
                if (isNotForm4(code)) continue;
                // retrieve and save filing data
                String href = parts[1].replace("-index.html", "");
                String accessionNo = href.substring(href.lastIndexOf("/") + 1);
                if (parts[0].contains("<hr>")) parts[0] = parts[0].split("<hr>")[1];
                Date date = DateUtils.parse(parts[0].substring(0, parts[0].indexOf(" ")), "MM-dd-yyyy");
                String url = SEC_URL + href + ".txt";
                form4s.add(new Form4(accessionNo, date, url));
            }
        } catch (IOException e) {
            System.out.printf("[ERROR] Getting filings %d days ago: %s\n", daysAgo, e.getMessage());
        }
        return form4s;
    }

    private JsonNode getFilingData(String url) throws IOException {
        String source = getTextByUrl(url);
        String data = source.substring(source.indexOf("<XML>") + 1, source.indexOf("</XML>"));
        data = data.substring(data.indexOf("\n") + 1);
        return new XmlMapper().readTree(data.getBytes());
    }

    // TODO: Handle filing amendments ("4/A" code).
    private boolean isNotForm4(String code) {
        return !code.equals("4");
    }
}

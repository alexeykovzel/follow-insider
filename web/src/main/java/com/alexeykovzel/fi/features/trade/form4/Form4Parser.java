package com.alexeykovzel.fi.features.trade.form4;

import com.alexeykovzel.fi.features.stock.Stock;
import com.alexeykovzel.fi.features.insider.Insider;
import com.alexeykovzel.fi.features.trade.Trade;
import com.alexeykovzel.fi.features.trade.TradeCode;
import com.alexeykovzel.fi.common.DateUtils;
import com.alexeykovzel.fi.common.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Form4Parser extends JsonParser {

    public String getIssuerCik(JsonNode root) {
        return root.get("issuer").get("issuerCik").asText();
    }

    public Stock getFilingIssuer(JsonNode root) {
        return Stock.builder()
                .name(root.get("issuer").get("issuerName").asText())
                .cik(getIssuerCik(root))
                .build();
    }

    public Collection<Trade> getTrades(JsonNode root) {
        Collection<Trade> trades = new HashSet<>();
        for (boolean isDerivative : new boolean[]{true, false}) {
            String tableTag = isDerivative ? "derivativeTable" : "nonDerivativeTable";
            String tradesTag = isDerivative ? "derivativeTransaction" : "nonDerivativeTransaction";
            JsonNode table = root.has(tableTag) ? root.get(tableTag).get(tradesTag) : null;
            if (table != null) acceptNode(table, (row) -> trades.add(getTrade(row)));
        }
        return trades;
    }

    public Collection<Insider> getReportingInsiders(JsonNode root) {
        Collection<Insider> insiders = new HashSet<>();
        acceptNode(root.get("reportingOwner"), (owner) -> insiders.add(Insider.builder()
                .cik(owner.get("reportingOwnerId").get("rptOwnerCik").asText())
                .name(owner.get("reportingOwnerId").get("rptOwnerName").asText())
                .positions(getInsiderPositions(owner.get("reportingOwnerRelationship")))
                .build()));
        return insiders;
    }

    private Trade getTrade(JsonNode root) {
        JsonNode amounts = root.get("transactionAmounts");
        JsonNode shares = amounts.get("transactionShares");
        JsonNode postAmounts = root.get("postTransactionAmounts");
        JsonNode owned = postAmounts.get("sharesOwnedFollowingTransaction");
        JsonNode price = amounts.get("transactionPricePerShare").get("value");
        JsonNode exercisePrice = root.get("conversionOrExercisePrice");
        JsonNode ownership = root.get("ownershipNature").get("directOrIndirectOwnership");

        // TODO: Handle "share count vs total value" issue.
        shares = (shares == null) ? amounts.get("transactionTotalValue") : shares;
        owned = (owned == null) ? postAmounts.get("valueOwnedFollowingTransaction") : owned;
        price = (price == null && exercisePrice != null) ? exercisePrice.get("value") : price;

        return Trade.builder()
                .date(DateUtils.parseEdgar(root.get("transactionDate").get("value").asText()))
                .code(TradeCode.ofValue(root.get("transactionCoding").get("transactionCode").asText()))
                .securityTitle(root.get("securityTitle").get("value").asText())
                .isDirect(ownership.get("value").asText().equals("D"))
                .sharePrice((price != null) ? price.asDouble() : 0)
                .leftShares(owned.get("value").asDouble())
                .shareCount(shares.get("value").asDouble())
                .build();
    }

    private Set<String> getInsiderPositions(JsonNode root) {
        Set<String> positions = new HashSet<>();
        if (hasOne(root.get("isDirector"))) positions.add("Director");
        if (hasOne(root.get("isTenPercentOwner"))) positions.add("10% Owner");
        if (hasOne(root.get("isOfficer"))) {
            String title = root.get("officerTitle").asText();
            if (!title.toLowerCase().contains("remarks")) {
                positions.add(title);
            }
        }
        return positions;
    }
}

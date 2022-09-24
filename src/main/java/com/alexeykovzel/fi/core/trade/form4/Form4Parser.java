package com.alexeykovzel.fi.core.trade.form4;

import com.alexeykovzel.fi.core.stock.Stock;
import com.alexeykovzel.fi.core.insider.Insider;
import com.alexeykovzel.fi.core.trade.Trade;
import com.alexeykovzel.fi.utils.DateUtils;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Form4Parser {
    public String getIssuerCik(JsonNode root) {
        return root.get("issuer").get("issuerCik").asText();
    }

    public Stock getFilingIssuer(JsonNode root) {
        return Stock.builder()
                .name(root.get("issuer").get("issuerName").asText())
                .cik(getIssuerCik(root))
                .build();
    }

    public Collection<Trade> getTransactions(JsonNode root) {
        Collection<Trade> trades = new HashSet<>();
        for (boolean isDerivative : new boolean[]{true, false}) {
            String tableTag = isDerivative ? "derivativeTable" : "nonDerivativeTable";
            String transactionsTag = isDerivative ? "derivativeTransaction" : "nonDerivativeTransaction";
            JsonNode table = root.has(tableTag) ? root.get(tableTag).get(transactionsTag) : null;
            if (table != null) handleArrayNode(table, (row) -> trades.add(getTransaction(row)));
        }
        return trades;
    }

    public Collection<Insider> getReportingInsiders(JsonNode root) {
        Collection<Insider> insiders = new HashSet<>();
        handleArrayNode(root.get("reportingOwner"), (owner) -> insiders.add(Insider.builder()
                .cik(owner.get("reportingOwnerId").get("rptOwnerCik").asText())
                .name(owner.get("reportingOwnerId").get("rptOwnerName").asText())
                .positions(getInsiderPositions(owner.get("reportingOwnerRelationship")))
                .build()));
        return insiders;
    }

    private Trade getTransaction(JsonNode root) {
        JsonNode amounts = root.get("transactionAmounts");
        JsonNode shares = amounts.get("transactionShares");
        JsonNode postAmounts = root.get("postTransactionAmounts");
        JsonNode owned = postAmounts.get("sharesOwnedFollowingTransaction");
        JsonNode price = amounts.get("transactionPricePerShare").get("value");
        JsonNode exercisePrice = root.get("conversionOrExercisePrice");
        JsonNode ownership = root.get("ownershipNature").get("directOrIndirectOwnership");

        // TODO: Handle share count vs total value.
        shares = (shares == null) ? amounts.get("transactionTotalValue") : shares;
        owned = (owned == null) ? postAmounts.get("valueOwnedFollowingTransaction") : owned;
        price = (price == null && exercisePrice != null) ? exercisePrice.get("value") : price;

        return Trade.builder()
                .date(DateUtils.parseEdgar(root.get("transactionDate").get("value").asText()))
                .code(root.get("transactionCoding").get("transactionCode").asText())
                .securityTitle(root.get("securityTitle").get("value").asText())
                .isDirect(ownership.get("value").asText().equals("D"))
                .sharePrice((price != null) ? price.asDouble() : 0)
                .leftShares(owned.get("value").asDouble())
                .shareCount(shares.get("value").asDouble())
                .build();
    }

    private Set<String> getInsiderPositions(JsonNode root) {
        Set<String> positions = new HashSet<>();
//        if (hasOne(root.get("isOther"))) positions.add("Other");
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

    private boolean hasOne(JsonNode node) {
        return (node != null) && (node.asInt() == 1);
    }

    private void handleArrayNode(JsonNode node, Consumer<JsonNode> consumer) {
        if (node.isArray()) {
            for (JsonNode arrayNode : node) {
                consumer.accept(arrayNode);
            }
        } else {
            consumer.accept(node);
        }
    }
}

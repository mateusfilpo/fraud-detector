package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.mappers;

import br.com.filpo.frauddetector.domain.models.Merchant;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities.MerchantNode;

public final class MerchantMapper {

    private MerchantMapper() {
    }

    public static MerchantNode toNode(Merchant merchant) {
        return MerchantNode.builder()
                .merchantId(merchant.getMerchantId())
                .name(merchant.getName())
                .category(merchant.getCategory())
                .riskScore(merchant.getRiskScore())
                .build();
    }

    public static Merchant toDomain(MerchantNode node) {
        return new Merchant(
                node.getMerchantId(),
                node.getName(),
                node.getCategory(),
                node.getRiskScore());
    }
}
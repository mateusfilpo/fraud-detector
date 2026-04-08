package br.com.filpo.frauddetector.domain.ports.out;

import br.com.filpo.frauddetector.domain.models.Merchant;
import java.util.List;
import java.util.Optional;

public interface MerchantRepositoryPort {
    Merchant save(Merchant merchant);

    Optional<Merchant> findById(String merchantId);

    List<Merchant> findAll();

    boolean existsById(String merchantId);
}
package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.adapters;

import br.com.filpo.frauddetector.domain.models.Merchant;
import br.com.filpo.frauddetector.domain.ports.out.MerchantRepositoryPort;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.mappers.MerchantMapper;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.repositories.SpringDataMerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MerchantPersistenceAdapter implements MerchantRepositoryPort {

    private final SpringDataMerchantRepository repository;

    @Override
    public Merchant save(Merchant merchant) {
        var node = MerchantMapper.toNode(merchant);
        var saved = repository.save(node);
        return MerchantMapper.toDomain(saved);
    }

    @Override
    public Optional<Merchant> findById(String merchantId) {
        return repository.findById(merchantId)
                .map(MerchantMapper::toDomain);
    }

    @Override
    public List<Merchant> findAll() {
        return repository.findAll().stream()
                .map(MerchantMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(String merchantId) {
        return repository.existsById(merchantId);
    }
}
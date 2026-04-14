package br.com.filpo.frauddetector.infrastructure.config;

import br.com.filpo.frauddetector.domain.enums.AccountType;
import br.com.filpo.frauddetector.domain.enums.TransactionChannel;
import br.com.filpo.frauddetector.domain.models.*;
import br.com.filpo.frauddetector.domain.ports.in.AccountUseCase;
import br.com.filpo.frauddetector.domain.ports.in.TransactionUseCase;
import br.com.filpo.frauddetector.domain.ports.out.MerchantRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final AccountUseCase accountUseCase;
    private final TransactionUseCase transactionUseCase;
    private final MerchantRepositoryPort merchantRepositoryPort;
    private final Faker faker = new Faker();

    // Device compartilhado (para padrão SHARED_DEVICE)
    private static final String SHARED_FINGERPRINT = "SHARED-DEVICE-ABC123";
    private static final String SHARED_IP = "192.168.1.100";

    @Override
    public void run(String... args) {
        log.info("══════════════════════════════════════");
        log.info("  DATABASE SEEDER — Plantando dados");
        log.info("══════════════════════════════════════");

        // ── 1. Criar Merchants ──
        Merchant merchantLegit = merchantRepositoryPort.save(
                new Merchant("Loja ABC Eletrônicos", "electronics"));
        Merchant merchantSuspicious = merchantRepositoryPort.save(
                new Merchant("CryptoExchange XYZ", "crypto"));
        log.info("✅ {} merchants criados", 2);

        // ── 2. Criar Contas ──
        // Contas para anel de fraude (ring size 2)
        Account ringA = accountUseCase.createAccount("Carlos Ring-A", AccountType.CHECKING);
        Account ringB = accountUseCase.createAccount("Maria Ring-B", AccountType.CHECKING);

        // Contas para anel de fraude (ring size 3)
        Account ring3A = accountUseCase.createAccount("João Ring3-A", AccountType.CHECKING);
        Account ring3B = accountUseCase.createAccount("Ana Ring3-B", AccountType.SAVINGS);
        Account ring3C = accountUseCase.createAccount("Pedro Ring3-C", AccountType.CHECKING);

        // Conta laranja (orange account)
        Account orange = accountUseCase.createAccount("Laranja Silva", AccountType.CHECKING);
        Account orangeTarget = accountUseCase.createAccount("Beneficiário Final", AccountType.SAVINGS);
        List<Account> orangeSources = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            orangeSources.add(accountUseCase.createAccount(
                    faker.name().fullName() + " (Fonte " + (i + 1) + ")", AccountType.CHECKING));
        }

        // Contas para shared device
        Account sharedDevA = accountUseCase.createAccount("Device-User-A", AccountType.CHECKING);
        Account sharedDevB = accountUseCase.createAccount("Device-User-B", AccountType.SAVINGS);

        // Conta para impossible travel
        Account traveler = accountUseCase.createAccount("Viajante Impossível", AccountType.CHECKING);
        Account travelReceiver = accountUseCase.createAccount("Receptor Viagem", AccountType.CHECKING);

        // Contas novas para suspicious merchant
        List<Account> newAccounts = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            newAccounts.add(accountUseCase.createAccount(
                    faker.name().fullName() + " (Nova " + (i + 1) + ")", AccountType.CHECKING));
        }

        // Contas legítimas para volume
        List<Account> legitAccounts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            legitAccounts.add(accountUseCase.createAccount(
                    faker.name().fullName(), AccountType.CHECKING));
        }

        int totalAccounts = 6 + 5 + 3 + 2 + 4 + 5;
        log.info("✅ {} contas criadas", totalAccounts);

        // ── 3. Transações — Padrão: FRAUD_RING (size 2) ──
        Device deviceRing = new Device("ring-device-001", faker.internet().ipV4Address(),
                faker.internet().userAgent());
        Location locSP = new Location("São Paulo", "BR", -23.5505, -46.6333);

        transactionUseCase.createTransaction(
                new BigDecimal("5000.00"), TransactionChannel.WEB,
                ringA.getAccountId(), ringB.getAccountId(),
                deviceRing, locSP, merchantLegit.getMerchantId());
        transactionUseCase.createTransaction(
                new BigDecimal("4800.00"), TransactionChannel.WEB,
                ringB.getAccountId(), ringA.getAccountId(),
                deviceRing, locSP, null);
        log.info("🔄 Fraud Ring (size 2): {} → {} → {}", ringA.getAccountId().substring(0, 8),
                ringB.getAccountId().substring(0, 8), ringA.getAccountId().substring(0, 8));

        // ── 4. Transações — Padrão: FRAUD_RING (size 3) ──
        transactionUseCase.createTransaction(
                new BigDecimal("10000.00"), TransactionChannel.TRANSFER,
                ring3A.getAccountId(), ring3B.getAccountId(),
                null, locSP, null);
        transactionUseCase.createTransaction(
                new BigDecimal("9500.00"), TransactionChannel.TRANSFER,
                ring3B.getAccountId(), ring3C.getAccountId(),
                null, locSP, null);
        transactionUseCase.createTransaction(
                new BigDecimal("9000.00"), TransactionChannel.TRANSFER,
                ring3C.getAccountId(), ring3A.getAccountId(),
                null, locSP, null);
        log.info("🔄 Fraud Ring (size 3): A → B → C → A");

        // ── 5. Transações — Padrão: ORANGE_ACCOUNT ──
        for (Account source : orangeSources) {
            transactionUseCase.createTransaction(
                    new BigDecimal(faker.number().numberBetween(1000, 5000) + ".00"),
                    TransactionChannel.TRANSFER,
                    source.getAccountId(), orange.getAccountId(),
                    null, locSP, null);
        }
        transactionUseCase.createTransaction(
                new BigDecimal("25000.00"), TransactionChannel.TRANSFER,
                orange.getAccountId(), orangeTarget.getAccountId(),
                null, locSP, null);
        log.info("🍊 Orange Account: 6 fontes → Laranja → 1 destino");

        // ── 6. Transações — Padrão: SHARED_DEVICE ──
        Device sharedDevice = new Device(SHARED_FINGERPRINT, SHARED_IP, "Mozilla/5.0 Shared");
        transactionUseCase.createTransaction(
                new BigDecimal("2000.00"), TransactionChannel.WEB,
                sharedDevA.getAccountId(), legitAccounts.get(0).getAccountId(),
                sharedDevice, locSP, merchantLegit.getMerchantId());
        transactionUseCase.createTransaction(
                new BigDecimal("3000.00"), TransactionChannel.WEB,
                sharedDevB.getAccountId(), legitAccounts.get(1).getAccountId(),
                sharedDevice, locSP, null);
        log.info("📱 Shared Device: 2 contas no device {}", SHARED_FINGERPRINT);

        // ── 7. Transações — Padrão: IMPOSSIBLE_TRAVEL ──
        Location locTokyo = new Location("Tokyo", "JP", 35.6762, 139.6503);
        transactionUseCase.createTransaction(
                new BigDecimal("500.00"), TransactionChannel.POS,
                traveler.getAccountId(), travelReceiver.getAccountId(),
                new Device("travel-device-001", "10.0.0.1", "Chrome"),
                locSP, merchantLegit.getMerchantId());
        // Segunda transação em Tokyo (mesmo device, ~30 minutos depois — impossível)
        transactionUseCase.createTransaction(
                new BigDecimal("800.00"), TransactionChannel.POS,
                traveler.getAccountId(), travelReceiver.getAccountId(),
                new Device("travel-device-001", "10.0.0.2", "Chrome"),
                locTokyo, null);
        log.info("🌍 Impossible Travel: São Paulo → Tokyo em minutos");

        // ── 8. Transações — Padrão: SUSPICIOUS_MERCHANT ──
        for (Account newAcc : newAccounts) {
            transactionUseCase.createTransaction(
                    new BigDecimal(faker.number().numberBetween(100, 500) + ".00"),
                    TransactionChannel.WEB,
                    newAcc.getAccountId(), legitAccounts.get(2).getAccountId(),
                    null, locSP, merchantSuspicious.getMerchantId());
        }
        log.info("🏪 Suspicious Merchant: {} contas novas → {}", newAccounts.size(),
                merchantSuspicious.getName());

        // ── 9. Transações legítimas (volume) ──
        int legitCount = 0;
        for (int i = 0; i < legitAccounts.size() - 1; i++) {
            for (int j = i + 1; j < legitAccounts.size(); j++) {
                transactionUseCase.createTransaction(
                        new BigDecimal(faker.number().numberBetween(50, 2000) + ".00"),
                        TransactionChannel.values()[faker.number().numberBetween(0, 5)],
                        legitAccounts.get(i).getAccountId(),
                        legitAccounts.get(j).getAccountId(),
                        new Device(faker.internet().macAddress(), faker.internet().ipV4Address(),
                                faker.internet().userAgent()),
                        new Location(faker.address().city(), "BR",
                                Double.parseDouble(faker.address().latitude()),
                                Double.parseDouble(faker.address().longitude())),
                        merchantLegit.getMerchantId());
                legitCount++;
            }
        }
        log.info("✅ {} transações legítimas criadas", legitCount);

        log.info("══════════════════════════════════════");
        log.info("  SEED COMPLETO — Padrões plantados:");
        log.info("  🔄 2 Fraud Rings (size 2 e 3)");
        log.info("  🍊 1 Orange Account (6→1→1)");
        log.info("  📱 1 Shared Device");
        log.info("  🌍 1 Impossible Travel (SP→Tokyo)");
        log.info("  🏪 1 Suspicious Merchant (4 novas)");
        log.info("══════════════════════════════════════");
    }
}
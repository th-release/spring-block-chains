package com.threlease.base.utils.blockchains;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.Optional;

public class Wallet {
    public String account;
    public String privateKey;
    public String publicKey;
    public double balance;

    public Wallet(Optional<String> privateKey) {
        this.privateKey = privateKey.orElseGet(this::getPrivateKey);
        this.publicKey = Arrays.toString(getPublicKey());
        this.account = getAccount();
        this.balance = 0;

        createWallet(this);
    }

    private String getPrivateKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] privateKeyBytes = new byte[32];
        secureRandom.nextBytes(privateKeyBytes);
        return Hex.toHexString(privateKeyBytes);
    }

    public byte[] getPublicKey() {
        Security.addProvider(new BouncyCastleProvider());
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
            keyPairGenerator.initialize(ecSpec);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            return keyPair.getPublic().getEncoded();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // 계정 생성
    private String getAccount() {
        byte[] publicKeyBytes = publicKey.getBytes(StandardCharsets.UTF_8);
        byte[] accountBytes = new byte[20];
        System.arraycopy(publicKeyBytes, 26, accountBytes, 0, 20);
        return Hex.toHexString(accountBytes);
    }

    private void createWallet(Wallet wallet) {
        String currentDirectory = System.getProperty("user.dir");
        File dir = new File(currentDirectory + "/data/wallets");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, wallet.getAccount());
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(wallet.getPrivateKey());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

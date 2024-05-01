package com.threlease.base.functions.wallet;

import com.threlease.base.utils.blockchains.Wallet;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/wallet")
@AllArgsConstructor
public class WalletController {

    @PostMapping("/create")
    public Wallet createWallet() {
        return new Wallet(Optional.empty());
    }
}

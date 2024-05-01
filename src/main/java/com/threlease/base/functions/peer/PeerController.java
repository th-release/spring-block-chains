package com.threlease.base.functions.peer;

import com.threlease.base.utils.Failable;
import com.threlease.base.utils.blockchains.Peer;
import com.threlease.base.utils.responses.BasicResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Optional;

@RestController
@RequestMapping("/peer")
@AllArgsConstructor
public class PeerController {
    private final Peer peers;

    @PostMapping("/create")
    public ResponseEntity<BasicResponse> createPeer(
            HttpServletRequest request
    ) {
        Failable<Boolean, String> addRequest = peers.addPeer(request.getRemoteAddr());

        if (addRequest.isError())
            return ResponseEntity.status(500).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.ofNullable(addRequest.getError()))
                            .build()
            );

        return ResponseEntity.status(201).body(
                BasicResponse.builder()
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BasicResponse> getPeer(
            @PathVariable("id") String id
    ) {
        Failable<File, String> peer = peers.getPeer(id);

        if (peer.isError())
            return ResponseEntity.status(500).body(
                    BasicResponse.builder()
                            .success(false)
                            .message(Optional.ofNullable(peer.getError()))
                            .build()
            );

        return ResponseEntity.status(200).body(
                BasicResponse.builder()
                        .success(true)
                        .data(Optional.of(peer.getValue().getName()))
                        .build()
        );
    }
}

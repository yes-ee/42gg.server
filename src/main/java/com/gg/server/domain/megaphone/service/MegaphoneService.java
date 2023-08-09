package com.gg.server.domain.megaphone.service;

import com.gg.server.domain.megaphone.data.Megaphone;
import com.gg.server.domain.megaphone.data.MegaphoneRepository;
import com.gg.server.domain.megaphone.dto.MegaphoneUseRequestDto;
import com.gg.server.domain.megaphone.exception.MegaphoneTimeException;
import com.gg.server.domain.megaphone.redis.MegaphoneRedis;
import com.gg.server.domain.megaphone.redis.MegaphoneRedisRepository;
import com.gg.server.domain.receipt.data.Receipt;
import com.gg.server.domain.receipt.data.ReceiptRepository;
import com.gg.server.domain.receipt.exception.ItemStatusException;
import com.gg.server.domain.receipt.exception.ReceiptNotFoundException;
import com.gg.server.domain.receipt.exception.ReceiptNotOwnerException;
import com.gg.server.domain.receipt.type.ItemStatus;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MegaphoneService {
    private final UserRepository userRepository;
    private final ReceiptRepository receiptRepository;
    private final MegaphoneRepository megaphoneRepository;
    private final MegaphoneRedisRepository megaphoneRedisRepository;

    @Transactional
    public void useMegaphone(MegaphoneUseRequestDto megaphoneUseRequestDto, UserDto user) {
        User loginUser = userRepository.findById(user.getId()).orElseThrow(() -> new UsernameNotFoundException("User" + user.getId()));
        if (LocalTime.now().isAfter(LocalTime.of(23, 55)) || LocalTime.now().isBefore(LocalTime.of(0, 5))) {
            throw new MegaphoneTimeException();
        }
        Receipt receipt = receiptRepository.findById(megaphoneUseRequestDto.getReceiptId()).orElseThrow(ReceiptNotFoundException::new);
        if (!receipt.getOwnerIntraId().equals(user.getIntraId())) {
            throw new ReceiptNotOwnerException();
        }
        if (!receipt.getStatus().equals(ItemStatus.BEFORE)) {
            throw new ItemStatusException();
        }
        receipt.updateStatus(ItemStatus.WAITING);
        Megaphone megaphone = new Megaphone(loginUser, receipt, megaphoneUseRequestDto.getContent(), LocalDate.now().plusDays(1));
        megaphoneRepository.save(megaphone);
    }

    @Transactional
    public void setMegaphoneList(LocalDate today) {
        megaphoneRepository.findAllByUsedAt(today).forEach(megaphone -> megaphone.getReceipt().updateStatus(ItemStatus.USED));
        megaphoneRedisRepository.deleteAllMegaphone();
        List<Megaphone> megaphones = megaphoneRepository.findAllByUsedAt(today.plusDays(1));
        for (Megaphone megaphone : megaphones) {
            megaphone.getReceipt().updateStatus(ItemStatus.USING);
            megaphoneRedisRepository.addMegaphone(new MegaphoneRedis(megaphone.getId(), megaphone.getUser().getIntraId(), megaphone.getContent(),
                    LocalDateTime.of(megaphone.getUsedAt(), LocalTime.of(0, 0))));
        }
    }
}
package com.gg.server.admin.item.service;

import com.gg.server.admin.item.data.ItemAdminRepository;
import com.gg.server.admin.item.dto.ItemDeleteRequestDto;
import com.gg.server.admin.item.dto.ItemHistoryResponseDto;
import com.gg.server.admin.item.dto.ItemListResponseDto;
import com.gg.server.admin.item.dto.ItemUpdateRequestDto;
import com.gg.server.admin.item.exception.ItemNotFoundException;
import com.gg.server.domain.item.data.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ItemAdminService {
    private final ItemAdminRepository itemAdminRepository;
    @Transactional(readOnly = true)
    public ItemListResponseDto getAllItemHistory(Pageable pageable) {
        Page<ItemHistoryResponseDto> responseDtos = itemAdminRepository.findAll(pageable).map(ItemHistoryResponseDto::new);
        return new ItemListResponseDto(responseDtos.getContent(), responseDtos.getTotalPages());
    }

    @Transactional
    public void updateItem(Long itemId, ItemUpdateRequestDto itemUpdateRequestDto) {
        Item item = itemAdminRepository.findById(itemId).orElseThrow(()-> new ItemNotFoundException());
        item.setIsVisible(false);
        item.setDeleterIntraId(itemUpdateRequestDto.getCreatorIntraId());
        Item newItem = new Item(itemUpdateRequestDto);
        itemAdminRepository.save(newItem);
    }

    @Transactional
    public void deleteItem(Long itemId, ItemDeleteRequestDto itemDeleteRequestDto) {
        Item item = itemAdminRepository.findById(itemId).orElseThrow(()-> new ItemNotFoundException());
        item.setIsVisible(false);
        item.setDeleterIntraId(itemDeleteRequestDto.getDeleterIntraId());
    }
}
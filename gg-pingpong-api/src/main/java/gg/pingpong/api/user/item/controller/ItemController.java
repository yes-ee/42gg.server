package gg.pingpong.api.user.item.controller;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gg.server.domain.item.dto.ItemGiftRequestDto;
import com.gg.server.domain.item.dto.ItemStoreListResponseDto;
import com.gg.server.domain.item.dto.UserItemListResponseDto;
import com.gg.server.domain.item.service.ItemService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.dto.PageRequestDto;
import com.gg.server.global.utils.argumentresolver.Login;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/items")
public class ItemController {

	private final ItemService itemService;

	@GetMapping("/store")
	public ItemStoreListResponseDto getAllItems() {
		return itemService.getAllItems();
	}

	@PostMapping("/purchases/{itemId}")
	public ResponseEntity purchaseItem(@PathVariable Long itemId,
		@Parameter(hidden = true) @Login UserDto userDto) {
		itemService.purchaseItem(itemId, userDto);
		return new ResponseEntity(HttpStatus.CREATED);
	}

	@PostMapping("/gift/{itemId}")
	public ResponseEntity giftItem(@PathVariable Long itemId,
		@RequestBody ItemGiftRequestDto recipient,
		@Parameter(hidden = true) @Login UserDto userDto) {
		itemService.giftItem(itemId, recipient.getOwnerId(), userDto);
		return new ResponseEntity(HttpStatus.CREATED);
	}

	@GetMapping
	public UserItemListResponseDto getItemByUser(@ModelAttribute @Valid PageRequestDto req,
		@Parameter(hidden = true) @Login UserDto userDto) {
		Pageable pageable = PageRequest.of(req.getPage() - 1, req.getSize(),
			Sort.by("createdAt").descending());
		return itemService.getItemByUser(userDto, pageable);
	}

}

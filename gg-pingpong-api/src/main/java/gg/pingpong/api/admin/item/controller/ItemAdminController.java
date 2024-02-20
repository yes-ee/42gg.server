package gg.pingpong.api.admin.item.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gg.server.admin.item.dto.ItemListResponseDto;
import com.gg.server.admin.item.dto.ItemUpdateRequestDto;
import com.gg.server.admin.item.service.ItemAdminService;
import com.gg.server.domain.item.exception.ItemImageLargeException;
import com.gg.server.domain.item.exception.ItemImageTypeException;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.dto.PageRequestDto;
import com.gg.server.global.utils.argumentresolver.Login;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/admin/items")
public class ItemAdminController {
	private final ItemAdminService itemAdminService;

	@GetMapping("/history")
	public ItemListResponseDto getItemHistory(@ModelAttribute @Valid PageRequestDto pageRequestDto) {
		Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1, pageRequestDto.getSize(),
			Sort.by("createdAt").descending());
		return itemAdminService.getAllItemHistory(pageable);
	}

	@PostMapping(path = "/{itemId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity updateItem(@PathVariable("itemId") Long itemId,
		@RequestPart @Valid ItemUpdateRequestDto updateItemInfo,
		@RequestPart(required = false) MultipartFile imgData,
		@Parameter(hidden = true) @Login UserDto user) throws IOException {
		if (imgData != null) {
			if (imgData.getSize() > 50000) {
				throw new ItemImageLargeException();
			} else if (imgData.getContentType() == null || !imgData.getContentType().equals("image/jpeg")) {
				throw new ItemImageTypeException();
			}
			itemAdminService.updateItem(itemId, updateItemInfo, imgData, user);
		} else {
			itemAdminService.updateItem(itemId, updateItemInfo, user);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping("/{itemId}")
	public ResponseEntity deleteItem(@PathVariable("itemId") Long itemId,
		@Parameter(hidden = true) @Login UserDto user) {
		itemAdminService.deleteItem(itemId, user);
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}

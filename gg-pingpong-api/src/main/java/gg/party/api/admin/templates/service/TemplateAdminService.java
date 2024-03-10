package gg.party.api.admin.templates.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.party.Category;
import gg.data.party.GameTemplate;
import gg.party.api.admin.templates.controller.request.TemplateAdminCreateReqDto;
import gg.repo.party.CategoryRepository;
import gg.repo.party.TemplateRepository;
import gg.utils.exception.party.CategoryNotFoundException;
import gg.utils.exception.party.TemplateNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateAdminService {
	private final TemplateRepository templateRepository;
	private final CategoryRepository categoryRepository;

	/**
	 * 템플릿 추가
	 * @exception CategoryNotFoundException 존재하지 않는 카테고리 입력
	 */
	public void addTemplate(TemplateAdminCreateReqDto request) {
		Category category = categoryRepository.findById(request.getCategoryId())
			.orElseThrow(CategoryNotFoundException::new);

		GameTemplate gameTemplate = TemplateAdminCreateReqDto.toEntity(request, category);

		templateRepository.save(gameTemplate);
	}

	/**
	 * 템플릿 삭제
	 * @exception TemplateNotFoundException 존재하지 않는 템플릿 입력
	 */
	@Transactional
	public void removeTemplate(Long templateId) {
		if (!templateRepository.existsById(templateId)) {
			throw new TemplateNotFoundException();
		}
		templateRepository.deleteById(templateId);
	}
}
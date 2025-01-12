package gg.party.api.admin.template;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.auth.utils.AuthTokenProvider;
import gg.data.party.Category;
import gg.data.party.GameTemplate;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.admin.templates.controller.request.TemplateAdminCreateReqDto;
import gg.party.api.admin.templates.controller.request.TemplateAdminUpdateReqDto;
import gg.repo.party.CategoryRepository;
import gg.repo.party.TemplateRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class TemplateAdminControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AuthTokenProvider tokenProvider;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	TemplateRepository templateRepository;
	User userTester;
	String userAccessToken;
	Category testCategory;
	GameTemplate testTemplate;

	@Nested
	@DisplayName("템플릿 추가 테스트")
	class AddTemplate {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("adminTester", "adminTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
		}

		/**
		 * 템플릿을 추가
		 * 어드민만 할 수 있음.
		 */
		@Test
		@DisplayName("추가 성공 201")
		public void success() throws Exception {
			//given
			String url = "/party/admin/templates";
			TemplateAdminCreateReqDto templateAdminCreateReqDto = new TemplateAdminCreateReqDto(
				testCategory.getName(), "gameName", 4, 2,
				180, 180, "genre", "hard", "summary");
			String jsonRequest = objectMapper.writeValueAsString(templateAdminCreateReqDto);
			//when
			mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isCreated())
				.andReturn().getResponse();
			//then
			assertThat(templateRepository.findAll()).isNotNull();
		}

		@Test
		@DisplayName("카테고리 없음으로 인한 추가 실패 404")
		public void fail() throws Exception {
			//given
			String url = "/party/admin/templates";
			TemplateAdminCreateReqDto templateAdminCreateReqDto = new TemplateAdminCreateReqDto(
				"NOTFOUND", "gameName", 4, 2,
				180, 180, "genre", "hard", "summary");
			String jsonRequest = objectMapper.writeValueAsString(templateAdminCreateReqDto);
			//when && then
			mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("템플릿 수정 테스트")
	class UpdateTemplate {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("adminTester", "adminTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			testTemplate = testDataUtils.createNewTemplate(testCategory, "gameName", 4,
				2, 180, 180, "genre", "hard", "summary");
		}

		/**
		 * 템플릿 수정
		 * 어드민만 할 수 있음.
		 */
		@Test
		@DisplayName("수정 성공 204")
		public void success() throws Exception {
			//given
			String templateId = testTemplate.getId().toString();
			String url = "/party/admin/templates/" + templateId;
			Category newTestCategory = testDataUtils.createNewCategory("newCate");
			TemplateAdminUpdateReqDto templateAdminUpdateReqDto = new TemplateAdminUpdateReqDto(
				newTestCategory.getName(), "newGameName", 8, 4,
				90, 90, "newGenre", "easy", "newSummary");
			String jsonRequest = objectMapper.writeValueAsString(templateAdminUpdateReqDto);
			//when
			mockMvc.perform(patch(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isNoContent())
				.andReturn().getResponse();
			//then
			assertThat(testTemplate.getGameName()).isEqualTo(templateAdminUpdateReqDto.getGameName());
			assertThat(testTemplate.getCategory()).isEqualTo(newTestCategory);
			assertThat(testTemplate.getMaxGamePeople()).isEqualTo(templateAdminUpdateReqDto.getMaxGamePeople());
			assertThat(testTemplate.getMinGamePeople()).isEqualTo(templateAdminUpdateReqDto.getMinGamePeople());
			assertThat(testTemplate.getMaxGameTime()).isEqualTo(templateAdminUpdateReqDto.getMaxGameTime());
			assertThat(testTemplate.getMinGameTime()).isEqualTo(templateAdminUpdateReqDto.getMinGameTime());
			assertThat(testTemplate.getGenre()).isEqualTo(templateAdminUpdateReqDto.getGenre());
			assertThat(testTemplate.getDifficulty()).isEqualTo(templateAdminUpdateReqDto.getDifficulty());
			assertThat(testTemplate.getSummary()).isEqualTo(templateAdminUpdateReqDto.getSummary());
		}

		@Test
		@DisplayName("카테고리 없음으로 인한 수정 실패 404")
		public void noCategoryFail() throws Exception {
			//given
			String templateId = testTemplate.getId().toString();
			String url = "/party/admin/templates/" + templateId;
			TemplateAdminUpdateReqDto templateAdminUpdateReqDto = new TemplateAdminUpdateReqDto(
				"NOTFOUND", "newGameName", 8, 4,
				90, 90, "newGenre", "easy", "newSummary");
			String jsonRequest = objectMapper.writeValueAsString(templateAdminUpdateReqDto);
			//when && then
			mockMvc.perform(patch(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("최소인원이 최대인원보다 큰 오류 400")
		public void notValidMinMaxFail() throws Exception {
			//given
			String templateId = testTemplate.getId().toString();
			String url = "/party/admin/templates/" + templateId;
			TemplateAdminUpdateReqDto templateAdminUpdateReqDto = new TemplateAdminUpdateReqDto(
				testCategory.getName(), "newGameName", 4, 8,
				90, 90, "newGenre", "easy", "newSummary");
			String jsonRequest = objectMapper.writeValueAsString(templateAdminUpdateReqDto);
			//when && then
			mockMvc.perform(patch(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("템플릿 없음으로 인한 수정 실패 404")
		public void noTemplateFail() throws Exception {
			//given
			String templateId = "1000";
			String url = "/party/admin/templates/" + templateId;
			Category newTestCategory = testDataUtils.createNewCategory("newCate");
			TemplateAdminUpdateReqDto templateAdminUpdateReqDto = new TemplateAdminUpdateReqDto(
				newTestCategory.getName(), "newGameName", 8, 4,
				90, 90, "newGenre", "easy", "newSummary");
			String jsonRequest = objectMapper.writeValueAsString(templateAdminUpdateReqDto);
			//when && then
			String contentAsString = mockMvc.perform(patch(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isNotFound()).toString();
		}
	}

	@Nested
	@DisplayName("템플릿 삭제 테스트")
	class RemoveTemplate {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("adminTester", "adminTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			testTemplate = testDataUtils.createNewTemplate(testCategory, "gameName", 4,
				2, 180, 180, "genre", "hard", "summary");
		}

		/**
		 * 템플릿 삭제
		 * 어드민만 할 수 있음.
		 */
		@Test
		@DisplayName("삭제 성공 204")
		public void success() throws Exception {
			//given
			String templateId = testTemplate.getId().toString();
			String url = "/party/admin/templates/" + templateId;
			//when
			mockMvc.perform(delete(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isNoContent());
			//then
			assertThat(templateRepository.findAll()).isEmpty();
		}

		@Test
		@DisplayName("템플릿 없음으로 인한 삭제 실패 404")
		public void fail() throws Exception {
			//given
			String templateId = "10";
			String url = "/party/admin/templates/" + templateId;
			//when && then
			mockMvc.perform(delete(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isNotFound());
		}
	}
}

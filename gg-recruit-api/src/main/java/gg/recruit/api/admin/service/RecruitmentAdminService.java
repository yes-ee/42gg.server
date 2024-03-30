package gg.recruit.api.admin.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import gg.admin.repo.recruit.RecruitmentAdminRepository;
import gg.data.recruit.recruitment.CheckList;
import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.Recruitment;
import gg.data.recruit.recruitment.enums.InputType;
import gg.recruit.api.admin.service.dto.Form;
import gg.recruit.api.admin.service.dto.UpdateRecruitStatusParam;
import gg.utils.exception.custom.NotExistException;
import gg.utils.exception.recruitment.InvalidCheckListException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecruitmentAdminService {
	private final RecruitmentAdminRepository recruitmentAdminRepository;

	/**
	 * 채용 공고를 생성한다.
	 * Recruitment, Question, CheckList을 저장한다.
	 * @param recruitment Recruitment
	 * @param forms Question, CheckList이 포함
	 * @return Recruitment 생성된 채용 공고
	 */
	@Transactional
	public Recruitment createRecruitment(Recruitment recruitment, List<Form> forms) {
		for (int i = 0; i < forms.size(); i++) {
			Form form = forms.get(i);
			Question question = form.toQuestion(recruitment, i + 1);
			InputType inputType = question.getInputType();

			if (inputType == InputType.SINGLE_CHECK || inputType == InputType.MULTI_CHECK) {
				addCheckList(question, form.getCheckList());
			}
		}
		return recruitmentAdminRepository.save(recruitment);
	}

	/**
	 * @param question 질문
	 * @param checkList 선택지
	 * @throws InvalidCheckListException 선택지가 필요한데 비어있을 때 발생
	 */
	private void addCheckList(Question question, List<String> checkList) {
		if (checkList == null || checkList.isEmpty()) {
			throw new InvalidCheckListException();
		}
		for (String content : checkList) {
			new CheckList(question, content);
		}
	}

	@Transactional
	public void updateRecruitStatus(UpdateRecruitStatusParam updateRecruitStatusParam) {
		Recruitment recruitments = recruitmentAdminRepository.findById(updateRecruitStatusParam.getRecruitId())
			.orElseThrow(() -> new NotExistException("Recruitment not found."));
		recruitments.setFinish(updateRecruitStatusParam.getFinish());
	}
}

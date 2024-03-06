package gg.recruit.api.user.application.service.response;

import java.util.List;

import gg.data.recruit.application.Application;
import lombok.Getter;

@Getter
public class ApplicationListDto {
	List<ApplicationDto> applications;

	public ApplicationListDto(List<Application> applications) {
		this.applications = applications.stream().map(ApplicationDto::new).toList();
	}
}

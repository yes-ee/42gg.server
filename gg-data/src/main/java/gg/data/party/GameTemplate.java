package gg.data.party;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import gg.data.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class GameTemplate extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(length = 20)
	private String gameName;

	@Column
	private Integer maxGamePeople;

	@Column
	private Integer minGamePeople;

	@Column
	private Integer maxGameTime;

	@Column
	private Integer minGameTime;

	@Column(length = 10)
	private String genre;

	@Column(length = 10)
	private String difficulty;

	@Column(length = 100)
	private String summary;
}
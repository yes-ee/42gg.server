package gg.pingpong.admin.repo.pchange;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import gg.pingpong.data.game.PChange;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PChangeAdminRepositoryCustomImpl implements PChangeAdminRepositoryCustom {
	private final EntityManager em;

	@Override
	public List<PChange> findByTeamUser(Long userId) {
		String query = "SELECT p FROM PChange p WHERE p.user.id = :userId ORDER BY p.createdAt desc";
		return em.createQuery(query, PChange.class)
			.setParameter("userId", userId)
			.setMaxResults(2)
			.setLockMode(LockModeType.PESSIMISTIC_WRITE)
			.getResultList();
	}
}

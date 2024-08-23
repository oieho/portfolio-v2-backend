package com.oieho;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.oieho.entity.Member;
import com.oieho.entity.WorkBoard;
import com.oieho.entity.WorkComment;
import com.oieho.repository.WorkCommentRepository;

@SpringBootTest
public class WorkCommentRepositoryTests {
	@Autowired
	private WorkCommentRepository commentRepository;

	@Test
	@Transactional
	@Rollback(false) // 롤백하지 않도록 설정
	public void insertComment() {

		
		Integer[] depth = { 2, 2, 2, 1 };
		Integer[] depth2 = { 0, 0, 0, 1 }; // 댓글인지 아닌지 유무를 판단
		Integer[] face = { -2, -1, 0, 1, 2 };
		Long[] userNoRandom = LongStream.rangeClosed(1, 100).boxed().toArray(Long[]::new);
		Collections.shuffle(Arrays.asList(userNoRandom));

		Integer[] uidRandom = IntStream.rangeClosed(1, 12).boxed().toArray(Integer[]::new);
		Collections.shuffle(Arrays.asList(uidRandom));

		long rnum = 0;
		long rdepth = 0;
		long rdepthVar = 0;
		long uid = 0;
		for (long k = 1; k <= 40; k++) {
			int comments = (int) (Math.random() * 12);
			final long j = k;
			rdepthVar = 0;
			for (long i = 1; i <= comments; i++) {
				int count = (int) (Math.random() * 5); // 공감온도 5개 중에서 25% 확률로 하나 선정
				int count2 = (int) (Math.random() * 4);
				if (i == 1) { // 게시글의 첫 번째가 답변 글이 될 수 없음
					count2 = 0;
				}
				if(depth[count2] == 1) { // 25%의 확률로 rnum(댓글의 개수) 1씩 증가
				    if (rnum == 0) { // 댓글 개수가 0개이면 rdepth는 증가하지 않음
				        rdepth = rdepthVar;
				    }
				    rnum++;
				} else if(depth[count2] == 2) { // 75%의 확률로 else if에 진입하면 rnum을 0으로 초기화 및 rdepth를 1씩 증가[:wno의 첫 글의 rdepth는 무조건 1 증가]
				    rnum = 0;
				    rdepthVar++;
				    rdepth = rdepthVar;
				}
				LocalDateTime now = LocalDateTime.now();
				LocalDateTime randomDate = now.minusDays(new Random().nextInt(10)).withHour(new Random().nextInt(24))
						.withMinute(new Random().nextInt(60)).withSecond(new Random().nextInt(60));

				uid++;
				WorkComment workComment = WorkComment.builder()
						.member(Member.builder().userNo(userNoRandom[(int) (j + i - 1)]).build())
						.workBoard(WorkBoard.builder().wno(j).build()).uid((long) uid).depth((long) depth2[count2]).rnum(rnum).rdepth(rdepth)
						.face((long) face[count]).text("테스트 댓글을 입력" + i).regDate(randomDate).build();

			commentRepository.save(workComment);
			}
		}
	}
}

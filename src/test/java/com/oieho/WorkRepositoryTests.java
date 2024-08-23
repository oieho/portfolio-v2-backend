package com.oieho;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import com.oieho.entity.Category;
import com.oieho.entity.WorkBoard;
import com.oieho.entity.WorkImage;
import com.oieho.repository.WorkBoardRepository;
import com.oieho.repository.WorkImageRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
public class WorkRepositoryTests {

	@Autowired
	private WorkBoardRepository workRepository;

	@Autowired
	private WorkImageRepository imageRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	@Commit
	@Test
	public void insertArticles() {
		IntStream.rangeClosed(0, 40).forEach(i -> {
			Random random = new Random();
			int randomNumber = random.nextInt(99999);
//			int randomInt = (int) (Math.random() * 42); // 최초 랜덤 값 생성 실제 개수 보다 -1(0부터 시작)

			String[] title = { "Portfolio v1", "Portfolio v2", "Portfolio v3", "김치 브로셔", "쿠쿠 로고", "스키 로고", "박물관 큐비즘 로고",
					"여의도 불꽃축제 포스터", "United Korea 포스터", "안동 민속주 축제 포스터", "아리랑 포스터", "Global Korea 포스터", "삽화 다이어리",
					"식품 위생 포스터", "㈜일성수출포장 홈페이지", "세실수출포장 홈페이지", "맨하탄포티지 코리아 홈페이지", "TheSak 코리아 홈페이지", "웹 표준 홈페이지",
					"디자인의 역사 홈페이지", "미술관 홈페이지", "인물 캐리커처", "인물 캐리커처2", "우주에서 만난 오징어 캐릭터", "코치 상세페이지", "Anon 고글 상세페이지",
					"The Sak 상세페이지", "주방기기 상세페이지", "다리미판 상세페이지", "펀칭 볼바구니 상세페이지", "맨하탄포티지 상세페이지", "대형건조대 상세페이지",
					"스키 강습 페이지", "일식집 메뉴판", "락앤락 스마트팩 상세페이지", "맨하탄포티지 사은품페이지", "과학 잡지 네이처", "축구 잡지 Best Eleven",
					"맨하탄포티지 정품TAG 안내 페이지", "맨하탄포티지 홈페이지 배너", "이력서 샘플" };

			String[] portfolioContent = {
					"<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p><div class=\"se-component se-image-container __se__float-center\">  <figure> <a href=\"http://oieho.dothome.co.kr/p/\" data-image-link=\"image\"><img src=\"/boardImgs/portfolioImgs/4fabdc31-caea-4ab2-9685-e7b6f8fbf322_portfolioV1.jpg\" alt=\"\" data-rotate=\"\" data-image-link=\"http://oieho.dothome.co.kr/p/\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"img43.jpg\" data-file-size=\"354469\" data-origin=\",\" style=\"\">\r\n"
							+ "    </a> </figure></div><p><br>\r\n" + "</p>",
					"2번 게시글 본문 복사 후 붙여넣기", "3번 게시글 본문 복사 후 붙여넣기",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/2c6ec682-115c-4f13-bde2-121275e0b8dc_brochure.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"brochure.jpg\" data-file-size=\"1003050\" data-origin=\",\" style=\"\"></figure></div>",
					"<p>&nbsp;&nbsp;&nbsp;&nbsp;<br>\r\n"
							+ "</p><div class=\"se-component se-image-container __se__float-center\">    <figure> <img src=\"/boardImgs/portfolioImgs/19299b39-9fd7-4fd7-bdcc-fb4b7d3acbe4_cuckooLogo.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-size=\",\" data-align=\"center\" data-percentage=\"auto,auto\" data-file-name=\"logo.jpg\" data-file-size=\"139184\" data-origin=\",\" style=\"\">\r\n"
							+ "\r\n" + "  </figure></div>",
					"<p>&nbsp;&nbsp;</p><p><br>\r\n" + "</p><p><br>\r\n"
							+ "</p><div class=\"se-component se-image-container __se__float-center\">  <figure> <img src=\"/boardImgs/portfolioImgs/a2dda015-2f5b-48ce-9c80-e2e8a3764398_skiLogo.png\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"화면 캡처 2023-07-15 150831.png\" data-file-size=\"507909\" data-origin=\",\" style=\"\">\r\n"
							+ "  </figure></div><p><br>\r\n" + "</p>",
					"<p>&nbsp;</p><p><br></p><p><br></p><p><br></p><p><br></p><div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/1aea6474-282a-46f0-aafc-78b440d698fa_artmuseum.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"artmuseum.jpg\" data-file-size=\"8740\" data-origin=\",\" style=\"\"></figure></div><p><br></p>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/01febfc1-4a65-454b-bd8a-db2206b91412_fireworkfestival.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"fireworkfestival.jpg\" data-file-size=\"410215\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/c5edbbe8-0cc5-4071-ac95-f2a589276f0c_brochurethumbnail.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"brochurethumbnail.jpg\" data-file-size=\"74690\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/6b8351d7-5fea-419a-b7b2-ee7c095fd322_liquorfestival.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"liquorfestival.jpg\" data-file-size=\"1507685\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/4f49bd54-882e-4b7a-b09d-355edd12a2f1_arirang.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"arirang.jpg\" data-file-size=\"674048\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/42237a62-2433-44db-956c-6154962df9df_globalkorea.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"globalkorea.jpg\" data-file-size=\"641795\" data-origin=\",\" style=\"\"></figure></div>",
					"<p>&nbsp;</p><div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/9d4e44f7-cfac-4f4f-9e0f-e71f4ee2bf1f_diary.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"diary.jpg\" data-file-size=\"186324\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/370f6a4c-20f2-4c24-9198-9392445c2d60_advertising.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"advertising.jpg\" data-file-size=\"625898\" data-origin=\",\" style=\"\"></figure></div>",
					"<p>&nbsp;&nbsp;&nbsp;</p><p><br>\r\n"
							+ "</p><div class=\"se-component se-image-container __se__float-center\"><br>\r\n"
							+ "  <figure> <a target=\"_blank\" href=\"http://www.woodpack.co.kr/\" data-image-link=\"image\"><img src=\"/boardImgs/portfolioImgs/5f491b18-5660-4114-9a83-ff8327956d22_woodpack.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"woodpack.jpg\" data-file-size=\"697243\" data-origin=\",\" data-image-link=\"http://www.woodpack.co.kr/\" style=\"\"></a></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\">\r\n" + "  <figure>\r\n"
							+ "    <img src=\"http://52.78.70.226:3000/boardImgs/portfolioImgs/b36e1ba6-2fc8-4e63-9413-e082cb962d0f_sesil.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"sesil.png\" data-file-size=\"1736380\" data-origin=\",\" data-image-link=\"\" style=\"\" data-index=\"0\" data-rotatex=\"\" data-rotatey=\"\">\r\n"
							+ "  </figure>\r\n" + "</div>\r\n" + "\r\n" + "<p><br>\r\n" + "</p>\r\n" + "\r\n"
							+ "<hr class=\"__se__solid\">\r\n" + "\r\n" + "<p><br>\r\n" + "</p>\r\n" + "\r\n"
							+ "<div class=\"se-component se-image-container __se__float-none\">\r\n" + "  <figure>\r\n"
							+ "    <img src=\"http://52.78.70.226:3000/boardImgs/portfolioImgs/03340b04-dba4-4903-a541-8406d6b2fa87_sesil2.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-size=\",\" data-align=\"none\" data-percentage=\"auto,auto\" data-file-name=\"sesil2.jpg\" data-file-size=\"308359\" data-origin=\",\" data-image-link=\"\" style=\"\" data-index=\"1\" data-rotatex=\"\" data-rotatey=\"\">\r\n"
							+ "  </figure>\r\n" + "</div>\r\n" + "",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/6566f343-00c3-4928-9bd3-f5273037447d_ManhattanPortage renewal.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"ManhattanPortage renewal.jpg\" data-file-size=\"464504\" data-origin=\",\" style=\"\"></figure></div><div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/76a09108-9fb3-4b79-b506-8da77b3a5175_ManhattanPortage.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"ManhattanPortage.jpg\" data-file-size=\"490862\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/2e3079d2-7a68-4821-8297-f63e69321051_Thesak.co.kr.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"Thesak.co.kr.jpg\" data-file-size=\"566509\" data-origin=\",\" style=\"\"></figure></div>",
					"<p>&nbsp;</p><p><br></p><div class=\"se-component se-image-container __se__float-center\"><figure><a target=\"_blank\" href=\"http://oieho.dothome.co.kr/p2/index.html\" data-image-link=\"image\"><img src=\"/boardImgs/portfolioImgs/c63635e3-5f87-4b1b-9844-67f6eff5640d_oieho.jpg\" alt=\"\" data-rotate=\"\" data-image-link=\"http://oieho.dothome.co.kr/p2/index.html\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"oieho.jpg\" data-file-size=\"730055\" data-origin=\",\" style=\"\"></a></figure></div><p><br></p>",
					"<p>&nbsp;</p><div class=\"se-component se-image-container __se__float-center\"><figure><a target=\"_blank\" href=\"http://oieho.dothome.co.kr/\" data-image-link=\"image\"><img src=\"/boardImgs/portfolioImgs/41a20ab4-8649-4cee-99c1-a221642f681a_historyofdesign.jpg\" alt=\"\" data-rotate=\"\" data-image-link=\"http://oieho.dothome.co.kr/\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"historyofdesign.jpg\" data-file-size=\"231802\" data-origin=\",\" style=\"\"></a></figure></div><p><br></p>",
					"<p>&nbsp;<br></p><p><br></p><div class=\"se-component se-image-container __se__float-center\"><figure><a target=\"_blank\" href=\"http://oieho.dothome.co.kr/p/html/main/artmuseum/index.html\" data-image-link=\"image\"><img src=\"/boardImgs/portfolioImgs/6ddd5049-bee0-4a4a-ae3f-651e64b8cfb2_artmuseum.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"artmuseum.jpg\" data-file-size=\"190941\" data-origin=\",\" data-image-link=\"http://oieho.dothome.co.kr/p/html/main/artmuseum/index.html\" style=\"\"></a></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/583c98b3-ab95-4458-b680-ef6d01effafb_caricature.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"caricature.jpg\" data-file-size=\"1832698\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/f05dfb1c-5a29-4d9d-8444-8ef4f6bec1a7_caricature2.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"caricature2.jpg\" data-file-size=\"173569\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/b0ab554b-5880-4153-ac80-7c495cc7b685_squid.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"squid.jpg\" data-file-size=\"79737\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/e382dfdb-dcc0-4d4e-b514-7eb103f9062c_coach.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"coach.jpg\" data-file-size=\"630759\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/67347927-67b3-4656-a406-671fdae68e1c_goggles.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"goggles.jpg\" data-file-size=\"959052\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/7a3eb2b8-2640-4977-9e0c-15411b5a9c3a_thesakdetail.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"thesakdetail.jpg\" data-file-size=\"667617\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/2d7a5bd5-5fde-4d6b-a718-f8ed5518912c_kitchen.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"kitchen.jpg\" data-file-size=\"301796\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/e75196f4-eccb-48d7-a192-357a0cd67529_ironingstand.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"ironingstand.jpg\" data-file-size=\"929296\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/23f960c4-7706-4739-a4fb-591aec6d6532_ballbasket.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"ballbasket.jpg\" data-file-size=\"662835\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/450f9f40-8dee-404f-a31a-c578a870b9d4_manhattandetailedpage.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"manhattandetailedpage.jpg\" data-file-size=\"3183185\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/56e6bfd3-8ad7-4416-9364-c1cf7b153d71_dryingrack.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"dryingrack.jpg\" data-file-size=\"522767\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/eace9b4b-5aa8-4d1b-84b2-ce3317db2206_ski.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"ski.jpg\" data-file-size=\"834333\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/dc458daa-7681-4e67-8b66-c623281a70e6_themenu.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"themenu.jpg\" data-file-size=\"505027\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/d64168cd-2fdd-4e7f-be3d-e0a9e1f8358a_smartbag.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"smartbag.jpg\" data-file-size=\"174287\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/cfb519a4-5307-46ab-8a79-b44b38075838_manhattanbonusgift.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"manhattanbonusgift.jpg\" data-file-size=\"495828\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/cc93cf61-1b82-4ce8-8585-68672152cce9_nature.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"nature.jpg\" data-file-size=\"532962\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/380dcb2a-8998-4aa9-9d00-926b637619ae_besteleven.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"besteleven.jpg\" data-file-size=\"343844\" data-origin=\",\" style=\"\"></figure></div>",
					"<div class=\"se-component se-image-container __se__float-center\"><figure><img src=\"/boardImgs/portfolioImgs/8c35abef-9533-4c0b-8da9-1c55dc96c908_manhattanportageascard.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"manhattanportageascard.jpg\" data-file-size=\"378502\" data-origin=\",\" style=\"\"></figure></div><p><br></p>",
					"<p><br>\r\n" + "</p>\r\n" + "\r\n" + "<p><br>\r\n" + "</p>\r\n" + "\r\n"
							+ "<div class=\"se-component se-image-container __se__float-center\">\r\n"
							+ "  <figure>\r\n"
							+ "    <img src=\"/boardImgs/portfolioImgs/33b1a041-8745-4e7d-baf3-6f943419ffbc_manhattanportageminibanner2.gif\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"manhattanportageminibanner2.gif\" data-file-size=\"307098\" data-origin=\",\" style=\"\" data-index=\"0\" data-rotatex=\"\" data-rotatey=\"\">\r\n"
							+ "  </figure>\r\n" + "</div>\r\n" + "\r\n" + "<p><br>\r\n" + "</p>\r\n" + "\r\n"
							+ "<div class=\"se-component se-image-container __se__float-center\">\r\n"
							+ "  <figure>\r\n"
							+ "    <img src=\"/boardImgs/2024/08/23/143f4fd5-4764-4bc7-9892-33b919f733e7_f837320a-72cb-4c15-b9d3-3a941e1bbf2a_manhattanportageminibanner1.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-size=\",\" data-align=\"center\" data-percentage=\"auto,auto\" data-file-name=\"f837320a-72cb-4c15-b9d3-3a941e1bbf2a_manhattanportageminibanner1.jpg\" data-file-size=\"30771\" data-origin=\",\" style=\"\" data-index=\"1\" data-rotatex=\"\" data-rotatey=\"\">\r\n"
							+ "  </figure>\r\n" + "</div>\r\n" + "\r\n" + "<p><br>\r\n" + "</p>\r\n" + "\r\n"
							+ "<div class=\"se-component se-image-container __se__float-center\">\r\n"
							+ "  <figure>\r\n"
							+ "    <img src=\"/boardImgs/2024/08/23/17d3b760-72f0-4fed-976d-7913e8bbfa7e_c68010f4-8056-4960-8172-12b85a7b1fac_manhattanportageminibanner3.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"c68010f4-8056-4960-8172-12b85a7b1fac_manhattanportageminibanner3.jpg\" data-file-size=\"61935\" data-origin=\",\" style=\"\" data-index=\"2\" data-rotatex=\"\" data-rotatey=\"\">\r\n"
							+ "  </figure>\r\n" + "</div>\r\n" + "",
					"<p>&nbsp;&nbsp;&nbsp;스크린샷만 업로드<br>\r\n"
							+ "</p><div class=\"se-component se-image-container __se__float-center\">  <figure> <img src=\"/boardImgs/portfolioImgs/0f35682c-2612-4373-9836-78dbe2572c34_resume.jpg\" alt=\"\" data-rotate=\"\" data-proportion=\"true\" data-align=\"center\" data-size=\",\" data-percentage=\"auto,auto\" data-file-name=\"img42.jpg\" data-file-size=\"376612\" data-origin=\",\" style=\"\">\r\n"
							+ "  </figure></div>" };

			String[] descriptionArr = { "썸네일 이미지를 클릭하면 포트폴리오 이미지를 돋보이게 제작",
					"모던하고 심플한 다이나믹 레이아웃 디자인 적용, 리덕스사가, MobX, thunk, JWT access/refresh token 기반 로그인, 소셜 로그인, 이메일 인증, 선택 및 다중정렬 및 댓글 답변형 및 이미지 게시판, 해시 태그",
					"예정",
					"첫 번째 페이지는 김치의 형상을 배경으로 표현하였으며, 두 번째 페이지 좌측에는 발효식품 김치에대한 설명과 우측에는 지구본에 뫼비우스의 띠를 합성하여 유기적인 김치의 가능성을 표현,세 번째 페이지 좌측에는 분무기에서 세균을 살상시키는 역동적인 동작과 우측에는 배추의 미래지향적 형상을 시각화",
					"쿠쿠의 대표 생산품인 밥솥의 버튼을 심볼화하여 아이덴티티 전략을 적용", "산, 고글, 글자로 웃는 얼굴을 형상화", "박물관 큐비즘 로고 제작",
					"다채로운 불꽃을 시각화하여 포스터 속에 축제 분위기를 조성", "종이 비행기로 태극기를 형상화, 구름으로 하트를 형상화하여 따뜻한 분위기를 구성",
					"중앙에 민속주 병을 위치하여 안동의 자랑인 하회탈을 형상화", "아리랑 고개로 넘어간다는 구절을 형상화",
					"GLOBAL 하나하나의 글자에 한국의 자랑스러운 역사적 이미지를 삽입", "복잡한 머리 속을 표현", "햄버거 사진 위쪽에 세균 카툰을 추가하여 세균의 위험성 경고",
					"로고의 그린,레드 계열을 중심으로 색상을 획일화하여 디자인, SEO적용 및 그누보드와 파워링크 연동",
					"로고의 삼각형을 기반으로 레이아웃 구성, SEO적용 및 그누보드 연동, 견적 문의 게시판 구현",
					"첫 번째 이미지는 기존 홈페이지의 컨셉을 유지하여 보수적으로 제작, 두 번째 이미지는 리뉴얼", "본사 홈페이지 기존 컨셉을 유지하여 보수적으로 제작", "웹 표준 코딩",
					"초록 계통 배경과 메인 이미지를 사용하여 창조적인 분위기 연출, 보색인 빨강색을 사용하여 메인메뉴에 포인트를 적용",
					"전반적으로 최대한 심플하게 제작, 스크롤을 내릴 경우 헤더배경이 검정으로 변하면서 큐비즘 로고의 흰 얼굴이 돋보이게 제작", "옛날 교복에 양말을 톡톡 튀는 빨강색으로 표현",
					"고상한 배경색에 파스텔톤의 색상으로 부드럽게 표현", "우주에서 포크를 들고 있는 오징어의 이색적인 모습을 연출하여 독창성을 극대화",
					"보라색과 핑크색을 좋아하는 싱가폴 사람들을 위한 상품 프로모션 페이지", "스키 도구에 연상되는 눈 분위기가 나는 흰 배경을 선정하여 제작", "브랜드 The Sak에 획일화",
					"잡지를 참고하여 제작", "사진이 돋보이는 깔끔한 페이지로 제작", "사진이 돋보이는 깔끔한 페이지로 제작", "상품을 클릭하면 팝업 레이어가 뜨는 형식으로 제작",
					"사진이 돋보이는 깔끔한 페이지로 제작", "사진의 블루,화이트톤을 바탕으로 표를 제작",
					"나무판 위에 도화지를 넣어 블랙과 화이트 계정의 색상을 주색으로 사용하여 일식집 분위기를 구성", "사진이 돋보이는 깔끔한 페이지로 제작",
					"기존 브랜드 이미지에 걸맞게 제작",
					"기존 Nature 잡지의 레이아웃을 유지하고 무궁화로 합성된 송수신기의 컨셉에 맞는 카피를 제작하여 한국의 송수신 기술력을 창조적으로 표현하여 세계에 홍보",
					"Best Eleven이라는 유명 축구 잡지를 배경으로 구성", "기존 브랜드 이미지에 걸맞게 제작",
					"가방이 움직이고 통통 튐으로서 활기 있는 분위기 조성, 기존 브랜드 이미지에 걸맞게 제작",
					"JSP를 사용하여 여러개의 input에 데이터를 담아 버튼을 누르면 해당 페이지를 캡쳐하여 이메일로 전송하는 폼형식", };

			Category[] categoryArr = { Category.홈페이지, Category.홈페이지, Category.홈페이지, Category.브로셔, Category.로고,
					Category.로고, Category.로고, Category.포스터, Category.포스터, Category.포스터, Category.포스터, Category.포스터,
					Category.기타, Category.포스터, Category.홈페이지, Category.홈페이지, Category.홈페이지, Category.홈페이지,
					Category.홈페이지, Category.홈페이지, Category.홈페이지, Category.캐릭터, Category.캐릭터, Category.캐릭터,
					Category.상세페이지, Category.상세페이지, Category.상세페이지, Category.상세페이지, Category.상세페이지, Category.상세페이지,
					Category.상세페이지, Category.상세페이지, Category.상세페이지, Category.상세페이지, Category.상세페이지, Category.상세페이지,
					Category.잡지, Category.잡지, Category.기타, Category.기타, Category.기타 };

			String[][] toolArr = { { "photoshop", "illustrator", "HTML", "CSS" },
					{ "photoshop", "illustrator", "vscode", "docker", "sts", "jpa", "jwt", "springboot", "javascript",
							"java", "gradle", "querydsl", "react", "redux", "mobx", "heidiSQL", "mariaDB", "redis",
							"jenkins" },
					{ "javascript" }, { "photoshop", "illustrator" }, { "illustrator" }, { "photoshop", "illustrator" },
					{ "illustrator" }, { "photoshop" }, { "photoshop" }, { "photoshop", "illustrator" },
					{ "photoshop", "illustrator" }, { "photoshop", "illustrator" }, { "illustrator" }, { "photoshop" },
					{ "photoshop", "illustrator", "dreamweaver", "HTML", "CSS", "jQuery" },
					{ "photoshop", "illustrator", "dreamweaver", "HTML", "CSS", "jQuery" },
					{ "photoshop", "illustrator", "dreamweaver", "HTML", "CSS", "jQuery" },
					{ "photoshop", "illustrator", "dreamweaver", "HTML", "CSS", "jQuery" },
					{ "photoshop", "illustrator", "dreamweaver", "HTML", "CSS", "jQuery" },
					{ "photoshop", "dreamweaver", "HTML", "CSS" },
					{ "photoshop", "illustrator", "dreamweaver", "javascript", "jQuery" },
					{ "photoshop", "illustrator" }, { "photoshop", "illustrator" }, { "photoshop", "illustrator" },
					{ "photoshop" }, { "photoshop" }, { "photoshop" }, { "photoshop" }, { "photoshop" },
					{ "photoshop" }, { "photoshop" }, { "photoshop" }, { "photoshop" }, { "photoshop", "illustrator" },
					{ "photoshop" }, { "photoshop" }, { "photoshop" }, { "photoshop" }, { "photoshop" },
					{ "photoshop" }, { "java", "HTML", "eclipse", "oracle", "jQuery" } };
			List<String> toolSet = new ArrayList<>();

			for (int j = 0; j < toolArr[i].length; j++) {
				toolSet.add(toolArr[i][j]);
			}

			String[][] tagStrArr = { { "포트폴리오", "테이블태그" }, { "포트폴리오", "블로그", "웹개발" }, { "예정", "Node.js", "Next.js" },
					{ "김치", "삽화", "아트워크" }, { "쿠쿠", "B.I", "C.I" }, { "스키", "고글" }, { "미술관", "큐비즘" },
					{ "아트워크", "심볼", "축제" }, { "아트워크", "종이비행기", "대한민국" }, { "축제", "삽화" }, { "대한민국", "삽화", "캘리그라피" },
					{ "3D", "대한민국" }, { "다이어리", "삽화" }, { "세균", "햄버거" }, { "그누보드", "파워링크", "SEO" }, { "그누보드", "견적문의" },
					{ "가방브랜드", "쇼핑몰", "메이크샵" }, { "가방브랜드", "쇼핑몰" }, { "웹표준", "포트폴리오" }, { "과제", "디자인역사" },
					{ "미술관", "다이나믹" }, { "캐리커처", "삽화" }, { "캐리커처", "삽화" }, { "삽화", "우주", "오징어" }, { "싱가폴", "퍼플", "핑크" },
					{ "스키", "고글" }, { "가방브랜드", "쇼핑몰" }, { "쇼핑몰", "주방기기", "오픈마켓" }, { "쇼핑몰", "다리미판", "오픈마켓" },
					{ "쇼핑몰", "볼바구니", "오픈마켓" }, { "쇼핑몰", "오픈마켓", "가방브랜드" }, { "쇼핑몰", "건조대", "오픈마켓" },
					{ "스키", "블루", "화이트" }, { "일식집", "메뉴판" }, { "쇼핑몰", "스마트백", "오픈마켓" }, { "가방브랜드", "사은품" },
					{ "Nature", "무궁화" }, { "BestEleven", "축구" }, { "가방브랜드", "정품" }, { "GIF", "가방브랜드", "배너" },
					{ "이력서", "메일" } };
			Set<String> tagSet2 = new LinkedHashSet<>();

			for (int j = 0; j < 41; j++) {
				String[] temp2 = tagStrArr[i];
				Collections.addAll(tagSet2, temp2);
			}

			WorkBoard work = WorkBoard.builder().title(title[i]).portfolioContent(portfolioContent[i])
					.description(descriptionArr[i]).category(categoryArr[i]).tools(toolSet).hashTag(tagSet2)
					.hits(randomNumber).build();
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime randomDate = now.minusDays(new Random().nextInt(30)).withHour(new Random().nextInt(24))
					.withMinute(new Random().nextInt(60)).withSecond(new Random().nextInt(60));
			work.setRegDate(randomDate);

			entityManager.persist(work);

			String[] uuid = { "29e1c494-b51e-4af7-b830-ffeeab668e59", "b3cad878-6a36-4636-b2f5-23f3535675bf",
					"4d205e14-4947-498c-b707-0e3de42fefb2", "e09042b7-9afe-43d5-9639-f450e08b550d",
					"9b92c003-cb04-4292-9081-1362902346c4", "d1229014-675d-4ba0-8096-8051b8224ec2",
					"973e5118-a542-4531-9645-d4dbb03e12a3", "1e9cc529-20f3-4236-9170-9e2b650498ef",
					"6107fbbb-1329-43f1-81ac-692285a786bf", "c827a31a-0afb-41a2-bbc3-15eeec2e60e2",
					"e4320995-ea1f-4c4e-b4dc-feb3b584cc2c", "f664eace-3760-4106-9017-169c3f94f739",
					"7fe056c8-0902-4c71-9707-c7469b1c4b6e", "6c8ae9db-aa67-489c-96b3-5701931ce73b",
					"c0abe569-a0f6-4f75-8c43-ed17dac6872b", "51678ef2-0d08-4818-b054-326c38a923dc",
					"51cacf4c-df90-4d49-9ae5-b606f6ce39aa", "d89853d9-81fc-4ffa-8b37-0b4ce62b6642",
					"4d9068e2-6151-495f-9455-2b76ea8dd85b", "efe7c821-b9ac-4a68-a423-cbfa13166d49",
					"8cd65457-9a40-4a14-be17-a2acff041cb7", "b5b4e686-1655-4046-8b57-d758509380a8",
					"1879dd12-7330-48b3-8ae6-880d5fa559c0", "970b5f25-0fd8-412c-a8f5-237f4e80c390",
					"3c02790d-781d-4aba-a411-532e46476787", "471abb78-7609-438b-b1a2-2be7bc8c012a",
					"3d4e2f32-e91b-4936-8fe8-da5527041f90", "b18b3f39-d4c3-4c34-9279-3240c6c4e30b",
					"6bf53a4f-a971-44e3-ad1d-16cdd7acf180", "6f880b0d-1613-4998-84cb-1894b2a38253",
					"c6de89f0-4a83-4b66-8935-b0606cdb3ed1", "c72ad9d1-5269-4bef-a141-898e4ec41a27",
					"00170af0-b2f7-4c94-aef4-453029318b01", "8dd37318-df83-4357-a68f-603ebf8d578d",
					"c9e8ac22-12ad-43dd-b1b6-582578718325", "04338bcf-364c-4ec5-9722-3cc080f8ed33",
					"27e7cbe1-e6c1-4416-8581-83087f9506d4", "1144aa38-4609-40ed-b074-c2fd65b96197",
					"fea44287-3a9c-49fb-b33f-5a2326347555", "e7f97867-3792-4009-83de-df696ce2e93b",
					"45e766c3-6362-4a88-921b-1128c70be36a" };
			WorkImage workImage = WorkImage.builder().path("static/boardImgs/thumbnails").uuid(uuid[i]).workBoard(work)
					.imgName("img" + (i + 1) + ".jpg").build();
			imageRepository.save(workImage);

			workRepository.save(work);

		});
	}
}

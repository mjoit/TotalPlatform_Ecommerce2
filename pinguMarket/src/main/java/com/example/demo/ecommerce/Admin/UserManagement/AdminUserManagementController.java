package com.example.demo.ecommerce.Admin.UserManagement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.ecommerce.Admin.AdminService;
import com.example.demo.ecommerce.Entity.User;
import com.example.demo.ecommerce.Paging.EzenPaging;
import com.example.demo.ecommerce.User.UserRepository;
import com.example.demo.ecommerce.User.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor //final 혹은 @NotNull이 붙은 필드의 생성자를 자동으로 만들어 줌
@Controller
@RequestMapping("/admin")
public class AdminUserManagementController {
	
	private final UserService us;
	private final UserRepository ur;
	private final AdminService adService;

	//---------------회원 관리 페이지(리스트)-----------------------------
	/* ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ 회원 조회 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ */
//	@PreAuthorize("isAuthenticated()") // 로그인 한 경우에만 요청 처리
	@GetMapping("/UserManagement") 
	public String AdminUserManagement(Model model, @RequestParam(value="page", defaultValue="0") int page, 
			@RequestParam(value = "kw", defaultValue = "") String kw,
			@RequestParam(value = "kwType", defaultValue = "") String kwType,
			HttpSession session) {
		
		//EzenPaging ezenPaging = new EzenPaging(현재 페이지 번호, 페이지당 글 갯수, 총 글 갯수, 페이징 버튼 갯수)
		EzenPaging ezenPaging = new EzenPaging(page, 10, adService.getUserCountByKeyword(kwType, kw), 5);
        List<User> UList = this.adService.getUserByKeyword(kwType, kw, ezenPaging.getStartNo(), ezenPaging.getPageSize());
        
        model.addAttribute("UList", UList);
        model.addAttribute("page", ezenPaging);
        model.addAttribute("kw", kw);
        model.addAttribute("kwType", kwType);
                          //" "안에 있는 값이 html에서 인식할 텍스트
        return "/Admin/AdminUserManagement";  
	}
	//---------------회원 관리 페이지(다중 선택 삭제)-----------------
//	@PreAuthorize("isAuthenticated()") // 로그인 한 경우에만 요청 처리
	@PostMapping("/User/delete")
	@ResponseBody
	public Map<String, Object> AdminUserDelete(@RequestBody Map<String, List<String>> payload) {
	    Map<String, Object> response = new HashMap<>();
	    List<String> ids = payload.get("ids"); 							//ids = id값들을 저장한 리스트
	    System.out.println("Received IDs: " + ids);
	    try {
	        for (String userIdStr : ids) {
	            int userId = Integer.parseInt(userIdStr); 				//저장한 json타입의 id값을 Integer타입으로 변환해 userId에 할당
	            System.out.println("id확인: " + userId);
	            User u = this.us.getUser(userId); 						//userId로 회원 데이터를 받아옴
	            this.us.delete(u);										//받아온 회원정보 삭제 
	        }
	        response.put("success", true);								//성공적으로 삭제
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);								//삭제 실패 시 false 값을 넘김(실패했습니다 알림창)
	    }
	    return response;
    }	
}

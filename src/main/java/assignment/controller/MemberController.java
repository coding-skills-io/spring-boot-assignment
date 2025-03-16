package assignment.controller;

import assignment.controller.request.MemberRequest;
import assignment.controller.response.MemberResponseBody;
import assignment.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/search")
    public MemberResponseBody getMember(@Valid @RequestBody final MemberRequest request) {
        return memberService.findMember(request);
    }

}

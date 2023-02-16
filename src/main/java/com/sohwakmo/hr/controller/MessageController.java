package com.sohwakmo.hr.controller;

import com.sohwakmo.hr.domain.Message;
import com.sohwakmo.hr.dto.MessageSearchDto;
import com.sohwakmo.hr.dto.MessageSendDto;
import com.sohwakmo.hr.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;

    /**
     * 쪽지 작성 페이지로 이동
     * @return
     */
    @GetMapping("/writeMessage")
    public String writeMessage() {
        log.info("writeMessage()");

        return "/message/writeMessage";
    }

    /**
     * 쪽지 보내기 기능
     * @param dto
     * @param files
     * @return
     * @throws IOException
     */
    @PostMapping("/sendMessage")
    public String sendMessage(MessageSendDto dto, @RequestParam("files") List<MultipartFile> files) throws IOException {
        log.info("sendMessage(dto = {}, files = {})", dto, files);

        messageService.create(dto, files);

        return "redirect:/message/receiveList";
    }

    /**
     * 페이징 처리
     * @param messageList
     * @param model
     */
    private void paging(Page<MessageSearchDto> messageList, Model model) {
        int startPage = (messageList.getPageable().getPageNumber() / 5) * 5 + 1;
        int endPage = Math.min(messageList.getTotalPages(), (messageList.getPageable().getPageNumber() / 5) * 5 + 5);

        if(endPage <= 0) {
            startPage = 1;
            endPage = 1;
        } else  {
            if(messageList.getTotalPages() < 6) {
                startPage = 1;
                endPage = messageList.getTotalPages();
            } else {
                if (messageList.getPageable().getPageNumber() < 5) {
                    startPage = 1;
                    endPage = 5;
                } else {
                    startPage = (messageList.getPageable().getPageNumber() / 5) * 5 + 1;
                    endPage = Math.min(messageList.getTotalPages(), (messageList.getPageable().getPageNumber() / 5) * 5 + 5);
                }
            }
        }

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
    }

    /**
     * 받은쪽지함으로 이동시키기
     * @param employeeNo
     * @param model
     * @return
     */
    @GetMapping("/receiveList")
    public String receiveList(String employeeNo, Model model, String messageType, String contentType, String keyword,
                              @PageableDefault(page = 0, size = 10, sort = "messageNo", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("receiveList(employeeNo = {}, messageType = {}, contentType = {}, keyword = {})", employeeNo, messageType, contentType, keyword);

        Page<MessageSearchDto> messageList;
        if(messageType == "") { messageType = null; }
        if(keyword == "") { keyword = null;}
        if(contentType == "") {contentType = null; }

        // 리스트로 바로 들어온 경우(검색하지 않은 경우)
        if(messageType == null && contentType == null && keyword == null) {
            log.info("검색하지 않은 경우");
            messageList = messageService.receiveListRead(employeeNo, pageable);
        } else {
            log.info("검색한 경우");
            messageList = messageService.receiveListSearchMessage(employeeNo, messageType, contentType, keyword, pageable);
        }

        log.info("messageList = {}", messageList);
        log.info("messageCount = {}", messageList.getTotalElements());

        paging(messageList, model);

        model.addAttribute("messageList", messageList);
        model.addAttribute("messageCount", messageList.getTotalElements());
        model.addAttribute("messageType", messageType);
        model.addAttribute("contentType", contentType);

        return "/message/receiveList";
    }

    /**
     * 받은쪽지함 쪽지 삭제하기
     * @param employeeNo
     * @param messageCheckBox
     * @return
     */
    @GetMapping("/receiveSendTrash")
    public String receiveSendTrash(String employeeNo, String[] messageCheckBox) {
        log.info("receiveSendTrash(employeeNo = {}, messageCheckBox = {})", employeeNo, messageCheckBox);

        messageService.receiveSendTrash(employeeNo, messageCheckBox);

        return "redirect:/message/receiveList";
    }

    /**
     * 보낸쪽지함 리스트 출력
     * @param employeeNo
     * @param model
     * @param messageType
     * @param contentType
     * @param keyword
     * @param pageable
     * @return
     */
    @GetMapping("/sendList")
    public String sendList(String employeeNo, Model model, String messageType, String contentType, String keyword,
                           @PageableDefault(page = 0, size = 10, sort = "messageNo", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("sendList(employeeNo = {}, messageType = {}, contentType = {}, keyword = {})", employeeNo, messageType, contentType, keyword);

        Page<MessageSearchDto> messageList;
        if(messageType == "") { messageType = null;}
        if(keyword == "") { keyword = null; }
        if(contentType == "") { contentType = null;}

        // 리스트로 바로 들어온 경우(검색하지 않은 경우)
        if(messageType == null && contentType == null && keyword == null) {
            log.info("검색하지 않은 경우");
            messageList = messageService.sendListRead(employeeNo, pageable);

        } else {
            log.info("검색한 경우");
            messageList = messageService.sendListSearchMessage(employeeNo, messageType, contentType, keyword, pageable);
        }

        log.info("messageList = {}", messageList);
        log.info("messageCount = {}", messageList.getTotalElements());

        paging(messageList, model);

        model.addAttribute("messageList", messageList);
        model.addAttribute("messageCount", messageList.getTotalElements());
        model.addAttribute("messageType", messageType);
        model.addAttribute("contentType", contentType);

        return "/message/sendList";
    }

    /**
     * 보낸쪽지함 쪽지 삭제하기
     * @param employeeNo
     * @param messageCheckBox
     * @return
     */
    @GetMapping("/senderSendTrash")
    public String senderSendTrash(String employeeNo, String[] messageCheckBox) {
        log.info("senderSendTrash(employeeNo = {}, messageCheckBox = {})", employeeNo, messageCheckBox);

        messageService.senderSendTrash(employeeNo, messageCheckBox);

        return "redirect:/message/sendList";
    }

    /**
     * 휴지통 리스트
     * @param employeeNo
     * @return
     */
    @GetMapping("/trashList")
    public String trashList(String employeeNo, Model model, String messageType, String contentType, String keyword,
                            @PageableDefault(page = 0, size = 10, sort = "messageNo", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("trashList(employeeNo = {}, messageType = {}, contentType = {}, keyword = {})", employeeNo, messageType, contentType, keyword);

        Page<MessageSearchDto> messageList;
        if(messageType == "") { messageType = null; }
        if(keyword == "") { keyword = null; }
        if(contentType == "") { contentType = null; }

        // 리스트로 바로 들어온 경우(검색하지 않은 경우)
        if(messageType == null && contentType == null && keyword == null) {
            log.info("검색하지 않은 경우");
            messageList = messageService.trashListRead(employeeNo, pageable);
        } else {
            log.info("검색한 경우");
            messageList = messageService.trashListSearchMessage(employeeNo, messageType, contentType, keyword, pageable);
        }

        log.info("messageList = {}", messageList);
        log.info("messageCount = {}", messageList.getTotalElements());

        paging(messageList, model);

        model.addAttribute("messageList", messageList);
        model.addAttribute("messageCount", messageList.getTotalElements());
        model.addAttribute("messageType", messageType);
        model.addAttribute("contentType", contentType);

        return "/message/trashList";
    }

    /**
     * 휴지통 쪽지 삭제하기
     * @param employeeNo
     * @param messageCheckBox
     * @return
     */
    @GetMapping("/trashSendDelete")
    public String trashSendDelete(String employeeNo, String[] messageCheckBox) {
        log.info("trashSendDelete(employeeNo = {}, messageCheckBox = {})", employeeNo, messageCheckBox);

        messageService.trashSendDelete(employeeNo, messageCheckBox);

        return "redirect:/message/trashList";
    }

    /**
     * 상세 보기 페이지
     * @param employeeNo
     * @param messageNo
     * @return
     */
    @GetMapping("/detailMessage")
    public String detailMessage(String employeeNo, Integer messageNo, Model model) {
        log.info("datailMessage(employeeNo = {}, messageNo = {})", employeeNo, messageNo);

        Message message = messageService.detailMessage(employeeNo, messageNo);
        log.info("message = {}", message);

        String isFile = null;
        if(message.getFileName1() != null) {
            log.info("1번 있음");
            isFile = "1";
        }
        if(message.getFileName2() != null) {
            log.info("2번 있음");
            isFile = "2";
        }
        if(message.getFileName3() != null) {
            log.info("3번 있음");
            isFile = "3";
        }

        log.info("isFile = {}", isFile);

        model.addAttribute("message", message);
        model.addAttribute("isFile", isFile);

        return "/message/detailMessage";
    }

    /**
     * 수신자 팝업창
     * @return
     */
    @GetMapping("/receiver")
    public String receiver() {
        log.info("receiver()");

        return "/message/receiver";
    }


}

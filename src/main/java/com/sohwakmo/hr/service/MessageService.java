package com.sohwakmo.hr.service;

import com.sohwakmo.hr.domain.Employee;
import com.sohwakmo.hr.domain.Message;
import com.sohwakmo.hr.dto.MessageSearchDto;
import com.sohwakmo.hr.dto.MessageSendDto;
import com.sohwakmo.hr.repository.EmployeeRepository;
import com.sohwakmo.hr.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * 메세지 보내기 기능 수행할 때 db에 저장하고 파일을 저장
     * @param dto
     * @param files
     * @throws IOException
     */
    public void create(MessageSendDto dto, List<MultipartFile> files) throws IOException {
        log.info("create(dto = {}, files = {})", dto, files);

        Employee senderEmployee = employeeRepository.findByEmployeeNo(dto.getSenderNo());
        log.info("sendEmployee = {}", senderEmployee);

        Employee receiveEmployee = employeeRepository.findByEmployeeNo(dto.getReceiveNo());
        log.info("receiveEmployee = {}", receiveEmployee);

        Message message = MessageSendDto.builder()
                .senderNo(dto.getSenderNo()).messageType(dto.getMessageType()).title(dto.getTitle()).receiveNo(dto.getReceiveNo()).content(dto.getContent())
                .senderEmployee(senderEmployee)
                .receiveEmployee(receiveEmployee)
                .build().toEntity();
        log.info("message = {}", message);

        for (MultipartFile multipartFile : files) {
            if(multipartFile.isEmpty()) {
                messageRepository.save(message);
            } else {
                saveFile(multipartFile, message);
                messageRepository.save(message);
            }
        }

    }

    /**
     * 파일을 저장하는 메서드
     * @param file
     * @param message
     * @throws IOException
     */
    public void saveFile(MultipartFile file, Message message) throws IOException {
        log.info("saveFile(file = {}, message = {})", file, message);

        // 파일 저장 경로
        String projectFilePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\message";
        log.info("projectFilePath = {}", projectFilePath);

        // 파일 이름
        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString() + "_" + file.getOriginalFilename();
        log.info("fileName = {}", fileName);

        File saveFile = new File(projectFilePath, fileName);

        // 파일을 해당경로에 저장
        file.transferTo(saveFile);

        if (message.getFilePath1() == null) {
            message.setFilePath1("/files/message" + "/" + fileName);
            message.setFileName1(file.getOriginalFilename());
            log.info("message = {}", message);
        } else if (message.getFilePath2() == null) {
            message.setFilePath2("/files/message" + "/" + fileName);
            message.setFileName2(file.getOriginalFilename());
            log.info("message = {}", message);
        } else if (message.getFilePath3() == null) {
            message.setFilePath3("/files/message" + "/" + fileName);
            message.setFileName3(file.getOriginalFilename());
            log.info("message = {}", message);
        }

    }

    /**
     * 받은쪽지함 들어가면 로그인한 번호로 받은쪽지 보여주기
     * @param employeeNo
     */
    public Page<MessageSearchDto> receiveListRead(String employeeNo, Pageable pageable) {
        log.info("receiveListRead(employeeNo = {})", employeeNo);

        Page<MessageSearchDto> messageList = messageRepository.findByReceiveNoOrderByMessageNoDesc(employeeNo, pageable);
        log.info("messageList = {}", messageList);
        log.info("messageContent = {}", messageList.getContent());

        return messageList;
    }

    /**
     * 받은쪽지함 검색
     * @param employeeNo
     * @param messageType
     * @param contentType
     * @param keyword
     * @return
     */
    public Page<MessageSearchDto> receiveListSearchMessage(String employeeNo, String messageType, String contentType, String keyword, Pageable pageable) {
        log.info("receiveListSearchMessage(employeeNo = {}, messageType = {}, contentType = {}, keyword = {})", employeeNo, messageType, contentType, keyword);

        Page<MessageSearchDto> messageSearchDtoList = null;

        if (messageType == null) { // 메세지 타입이 null인 경우
            log.info("null");
            switch(contentType) {
                case "all" :
                    log.info("findByReceiveNoAll");
                    messageSearchDtoList = messageRepository.findByReceiveNoAll(employeeNo, keyword, pageable);
                    break;
                case "title" :
                    log.info("findByReceiveNoAndTitle");
                    messageSearchDtoList = messageRepository.findByReceiveNoAndTitle(employeeNo, keyword, pageable);
                    break;
                case "sender" :
                    log.info("findByReceiveNoAndSenderName");
                    messageSearchDtoList = messageRepository.findByReceiveNoAndSenderName(employeeNo, keyword, pageable);
                    break;
            }
        } else { // 메세지 타입이 있는 경우
            log.info("not null");
            switch(contentType) {
                case "all" :
                    log.info("findByMessageTypeAndReceiveNoAll");
                    messageSearchDtoList = messageRepository.findByMessageTypeAndReceiveNoAll(employeeNo, keyword, messageType, pageable);
                    break;
                case "title" :
                    log.info("findByMessageTypeAndReceiveNoAndTitle");
                    messageSearchDtoList = messageRepository.findByMessageTypeAndReceiveNoAndTitle(employeeNo, keyword, messageType, pageable);
                    break;
                case "sender" :
                    log.info("findByMessageTypeAndReceiveNoAndName");
                    messageSearchDtoList = messageRepository.findByMessageTypeAndReceiveNoAndName(employeeNo, keyword, messageType, pageable);
                    break;
            }
        }

        log.info("messageSearchDtoList = {}", messageSearchDtoList);
        log.info("messageSearchDtoListContent = {}", messageSearchDtoList.getContent());

        return messageSearchDtoList;
    }

    /**
     * 받은쪽지함 삭제 버튼
     * @param employee
     * @param messageCheckBox
     */
    @Transactional
    public void receiveSendTrash(String employee, String[] messageCheckBox) {
        log.info("receiveSendTrash(employee = {}, messageCheckBox = {})", employee, messageCheckBox);

        for(String m : messageCheckBox){
            log.info("m = {}", m);
            Integer messageNo = Integer.parseInt(m);

            Message message = messageRepository.findById(messageNo).get();
            log.info("message = {}", message);

            message.setReceiveTrash(1);
            log.info("message = {}", message);
        }

    }

    /**
     * 보낸쪽지함 들어가면 로그인한 번호로 보낸쪽지 보여주기
     * @param employeeNo
     */
    public Page<MessageSearchDto> sendListRead(String employeeNo, Pageable pageable) {
        log.info("sendListRead(employeeNo = {})", employeeNo);

        Page<MessageSearchDto> messageList = messageRepository.findBySenderNoOrderByMessageNoDesc(employeeNo, pageable);
        log.info("messageList = {}", messageList);
        log.info("messageContent = {}", messageList.getContent());

        return messageList;
    }

    /**
     * 보낸쪽지함 검색
     * @param employeeNo
     * @param messageType
     * @param contentType
     * @param keyword
     * @return
     */
    public Page<MessageSearchDto> sendListSearchMessage(String employeeNo, String messageType, String contentType, String keyword, Pageable pageable) {
        log.info("receiveListSearchMessage(employeeNo = {}, messageType = {}, contentType = {}, keyword = {})", employeeNo, messageType, contentType, keyword);

        Page<MessageSearchDto> messageSearchDtoList = null;

        if (messageType == null) { // 메세지 타입이 null인 경우
            log.info("null");
            switch(contentType) {
                case "all" :
                    log.info("findByReceiveNoAll");
                    messageSearchDtoList = messageRepository.findBySenderNoAll(employeeNo, keyword, pageable);
                    break;
                case "title" :
                    log.info("findByReceiveNoAndTitle");
                    messageSearchDtoList = messageRepository.findBySenderNoAndTitle(employeeNo, keyword, pageable);
                    break;
                case "sender" :
                    log.info("findByReceiveNoAndSenderName");
                    messageSearchDtoList = messageRepository.findBySenderNoAndReceiveName(employeeNo, keyword, pageable);
                    break;
            }
        } else { // 메세지 타입이 있는 경우
            log.info("not null");
            switch(contentType) {
                case "all" :
                    log.info("findByMessageTypeAndReceiveNoAll");
                    messageSearchDtoList = messageRepository.findByMessageTypeAndSenderNoAll(employeeNo, keyword, messageType, pageable);
                    break;
                case "title" :
                    log.info("findByMessageTypeAndReceiveNoAndTitle");
                    messageSearchDtoList = messageRepository.findByMessageTypeAndSenderNoAndTitle(employeeNo, keyword, messageType, pageable);
                    break;
                case "sender" :
                    log.info("findByMessageTypeAndReceiveNoAndName");
                    messageSearchDtoList = messageRepository.findByMessageTypeAndSenderNoAndName(employeeNo, keyword, messageType, pageable);
                    break;
            }
        }

        log.info("messageSearchDtoList = {}", messageSearchDtoList);
        log.info("messageSearchDtoListContent = {}", messageSearchDtoList.getContent());

        return messageSearchDtoList;
    }

    /**
     * 보낸쪽지함 삭제 버튼
     * @param employee
     * @param messageCheckBox
     */
    @Transactional
    public void senderSendTrash(String employee, String[] messageCheckBox) {
        log.info("senderSendTrash(employee = {}, messageCheckBox = {})", employee, messageCheckBox);

        for(String m : messageCheckBox){
            log.info("m = {}", m);
            Integer messageNo = Integer.parseInt(m);

            Message message = messageRepository.findById(messageNo).get();
            log.info("message = {}", message);

            message.setSenderTrash(1);
            log.info("message = {}", message);
        }
    }

    /**
     * 휴지통 들어가면
     * @param employeeNo
     */
    public Page<MessageSearchDto> trashListRead(String employeeNo, Pageable pageable) {
        log.info("trashListRead(employeeNo = {})", employeeNo);

        Page<MessageSearchDto> messageList = messageRepository.findByEmployeeNoOrderByMessageNoDesc(employeeNo, pageable);
        log.info("messageList = {}", messageList);
        log.info("messageContent = {}", messageList.getContent());

        return messageList;
    }

    /**
     * 휴지통 검색
     * @param employeeNo
     * @param messageType
     * @param contentType
     * @param keyword
     * @return
     */
    public Page<MessageSearchDto> trashListSearchMessage(String employeeNo, String messageType, String contentType, String keyword, Pageable pageable) {
        log.info("trashListSearchMessage(employeeNo = {}, messageType = {}, contentType = {}, keyword = {})", employeeNo, messageType, contentType, keyword);

        Page<MessageSearchDto> messageSearchDtoList = null;

        if (messageType == null) { // 메세지 타입이 null인 경우
            log.info("null");
            switch(contentType) {
                case "all" :
                    log.info("findByReceiveNoAll");
                    messageSearchDtoList = messageRepository.findByEmployeeNoAll(employeeNo, keyword, pageable);
                    break;
                case "title" :
                    log.info("findByReceiveNoAndTitle");
                    messageSearchDtoList = messageRepository.findByEmployeeNoAndTitle(employeeNo, keyword, pageable);
                    break;
                case "sender" :
                    log.info("findByReceiveNoAndSenderName");
                    messageSearchDtoList = messageRepository.findByEmployeeNoAndReceiveName(employeeNo, keyword, pageable);
                    break;
            }
        } else { // 메세지 타입이 있는 경우
            log.info("not null");
            switch(contentType) {
                case "all" :
                    log.info("findByMessageTypeAndReceiveNoAll");
                    messageSearchDtoList = messageRepository.findByMessageTypeAndEmployeeNoAll(employeeNo, keyword, messageType, pageable);
                    break;
                case "title" :
                    log.info("findByMessageTypeAndReceiveNoAndTitle");
                    messageSearchDtoList = messageRepository.findByMessageTypeAndEmployeeNoAndTitle(employeeNo, keyword, messageType, pageable);
                    break;
                case "sender" :
                    log.info("findByMessageTypeAndReceiveNoAndName");
                    messageSearchDtoList = messageRepository.findByMessageTypeAndEmployeeNoAndName(employeeNo, keyword, messageType, pageable);
                    break;
            }
        }

        log.info("messageSearchDtoList = {}", messageSearchDtoList);
        log.info("messageSearchDtoListContent = {}", messageSearchDtoList.getContent());

        return messageSearchDtoList;
    }

    /**
     * 휴지통 삭제 버튼
     * @param employee
     * @param messageCheckBox
     */
    @Transactional
    public void trashSendDelete(String employee, String[] messageCheckBox) {
        log.info("trashSendDelete(employee = {}, messageCheckBox = {})", employee, messageCheckBox);

        for(String m : messageCheckBox){
            log.info("m = {}", m);
            Integer messageNo = Integer.parseInt(m);

            Message message = messageRepository.findById(messageNo).get();
            log.info("message = {}", message);

            if(message.getSenderNo().equals(employee)){
                log.info("보낸 메세지");
                message.setSenderDelete(1);
            }
            if(message.getReceiveNo().equals(employee)){
                log.info("받은 메세지");
                message.setReceiveDelete(1);
            }

            log.info("message = {}", message);
        }
    }

    /**
     * 디테일 페이지
     * @param employeeNo
     * @param messageNo
     * @return
     */
    @Transactional
    public Message detailMessage(String employeeNo, Integer messageNo) {
        log.info("detailMessage(employeeNo = {}, messageNo = {})", employeeNo, messageNo);

        Message message = messageRepository.findById(messageNo).get();
        log.info("message = {}", message);

        LocalDateTime currentLocalDateTime = LocalDateTime.now();
        log.info("currentLocalDateTime = {}", currentLocalDateTime);

        if(employeeNo.equals(message.getReceiveNo())) {
            log.info("true");
            message.setReceiveReadCheck(1);
            message.setReadTime(currentLocalDateTime);
            log.info("message = {}", message);
        }

        return message;
    }


}

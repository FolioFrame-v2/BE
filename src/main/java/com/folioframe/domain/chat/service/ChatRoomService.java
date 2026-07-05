package com.folioframe.domain.chat.service;

import com.folioframe.domain.chat.dto.request.ChatRoomCreateReqDTO;
import com.folioframe.domain.chat.dto.response.ChatMessageResDTO;
import com.folioframe.domain.chat.dto.response.ChatRoomListResDTO;
import com.folioframe.domain.chat.dto.response.ChatRoomResDTO;
import com.folioframe.domain.chat.entity.ChatMessage;
import com.folioframe.domain.chat.entity.ChatRoom;
import com.folioframe.domain.chat.entity.ChatRoomParticipant;
import com.folioframe.domain.chat.enums.ChatOriginType;
import com.folioframe.domain.chat.exception.ChatException;
import com.folioframe.domain.chat.exception.code.ChatErrorCode;
import com.folioframe.domain.chat.repository.ChatMessageRepository;
import com.folioframe.domain.chat.repository.ChatRoomListProjection;
import com.folioframe.domain.chat.repository.ChatRoomParticipantRepository;
import com.folioframe.domain.chat.repository.ChatRoomRepository;
import com.folioframe.domain.company.entity.CompanyProfile;
import com.folioframe.domain.company.exception.code.CompanyErrorCode;
import com.folioframe.domain.company.repository.CompanyProfileRepository;
import com.folioframe.domain.job.entity.JobPosting;
import com.folioframe.domain.job.exception.code.JobErrorCode;
import com.folioframe.domain.job.repository.JobPostingRepository;
import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.member.enums.MemberType;
import com.folioframe.domain.member.exception.code.MemberErrorCode;
import com.folioframe.domain.member.repository.MemberRepository;
import com.folioframe.domain.portfolio.entity.Portfolio;
import com.folioframe.domain.portfolio.exception.code.PortfolioErrorCode;
import com.folioframe.domain.portfolio.repository.PortfolioRepository;
import com.folioframe.domain.talent.entity.TalentProfile;
import com.folioframe.domain.talent.exception.code.TalentProfileErrorCode;
import com.folioframe.domain.talent.repository.TalentProfileRepository;
import com.folioframe.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final TalentProfileRepository talentProfileRepository;
    private final PortfolioRepository portfolioRepository;
    private final JobPostingRepository jobPostingRepository;

    @Transactional
    public ChatRoomResDTO getOrCreateRoom(ChatRoomCreateReqDTO request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

        ChatRoom room = (request.originType() == ChatOriginType.PORTFOLIO)
                ? getOrCreateRoomFromPortfolio(request.originId(), member)
                : getOrCreateRoomFromJobPosting(request.originId(), member);

        return toRoomResDTO(room, memberId);
    }

    private ChatRoom getOrCreateRoomFromPortfolio(Long portfolioId, Member member) {
        if (member.getMemberType() != MemberType.COMPANY) {
            throw new ChatException(ChatErrorCode.INVALID_ORIGIN_FOR_MEMBER_TYPE);
        }

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new GeneralException(PortfolioErrorCode.PORTFOLIO_NOT_FOUND));
        TalentProfile talentProfile = portfolio.getTalentProfile();
        CompanyProfile companyProfile = companyProfileRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new GeneralException(CompanyErrorCode.COMPANY_PROFILE_NOT_FOUND));

        return chatRoomRepository.findByCompanyProfileAndTalentProfileAndPortfolio(companyProfile, talentProfile, portfolio)
                .orElseGet(() -> createRoom(companyProfile, talentProfile, ChatOriginType.PORTFOLIO, portfolio, null));
    }

    private ChatRoom getOrCreateRoomFromJobPosting(Long jobPostingId, Member member) {
        if (member.getMemberType() != MemberType.TALENT) {
            throw new ChatException(ChatErrorCode.INVALID_ORIGIN_FOR_MEMBER_TYPE);
        }

        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new GeneralException(JobErrorCode.JOB_POSTING_NOT_FOUND));
        CompanyProfile companyProfile = jobPosting.getCompanyProfile();
        TalentProfile talentProfile = talentProfileRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new GeneralException(TalentProfileErrorCode.PROFILE_NOT_FOUND));

        return chatRoomRepository.findByCompanyProfileAndTalentProfileAndJobPosting(companyProfile, talentProfile, jobPosting)
                .orElseGet(() -> createRoom(companyProfile, talentProfile, ChatOriginType.JOB_POSTING, null, jobPosting));
    }

    private ChatRoom createRoom(CompanyProfile companyProfile, TalentProfile talentProfile,
                                 ChatOriginType originType, Portfolio portfolio, JobPosting jobPosting) {
        ChatRoom room = chatRoomRepository.save(ChatRoom.builder()
                .companyProfile(companyProfile)
                .talentProfile(talentProfile)
                .originType(originType)
                .portfolio(portfolio)
                .jobPosting(jobPosting)
                .build());

        participantRepository.save(ChatRoomParticipant.builder()
                .chatRoom(room)
                .memberId(companyProfile.getMember().getId())
                .build());
        participantRepository.save(ChatRoomParticipant.builder()
                .chatRoom(room)
                .memberId(talentProfile.getMember().getId())
                .build());

        return room;
    }

    public Page<ChatRoomListResDTO> getRoomList(Long memberId, String keyword, Pageable pageable) {
        Page<ChatRoomListProjection> page = chatRoomRepository.findRoomList(memberId, keyword, pageable);

        return page.map(projection -> {
            ChatRoom room = projection.chatRoom();
            boolean viewerIsCompany = isViewerCompanySide(room, memberId);

            return ChatRoomListResDTO.builder()
                    .chatRoomId(room.getId())
                    .counterpartName(viewerIsCompany ? room.getTalentProfile().getName() : room.getCompanyProfile().getCompanyName())
                    .counterpartType(viewerIsCompany ? MemberType.TALENT : MemberType.COMPANY)
                    .lastMessage(room.getLastMessagePreview())
                    .lastMessageAt(room.getLastMessageAt())
                    .unreadCount(projection.unreadCount())
                    .build();
        });
    }

    public ChatRoomResDTO getRoomDetail(Long roomId, Long memberId) {
        ChatRoom room = findRoomAsParticipant(roomId, memberId);
        return toRoomResDTO(room, memberId);
    }

    @Transactional
    public Page<ChatMessageResDTO> getMessages(Long roomId, Long memberId, Pageable pageable) {
        ChatRoom room = findRoomAsParticipant(roomId, memberId);

        ChatRoomParticipant participant = participantRepository.findByChatRoomAndMemberId(room, memberId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_ACCESS_DENIED));
        participant.resetUnreadCount();

        Page<ChatMessage> messages = chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(room, pageable);

        return messages.map(message -> ChatMessageResDTO.builder()
                .chatMessageId(message.getId())
                .senderId(message.getSenderId())
                .senderType(resolveSenderType(room, message.getSenderId()))
                .content(message.getContent())
                .sentAt(message.getCreatedAt())
                .build());
    }

    @Transactional
    public void hideRoom(Long roomId, Long memberId) {
        ChatRoom room = findRoomAsParticipant(roomId, memberId);
        ChatRoomParticipant participant = participantRepository.findByChatRoomAndMemberId(room, memberId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_ACCESS_DENIED));
        participant.hide();
    }

    private ChatRoom findRoomAsParticipant(Long roomId, Long memberId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!participantRepository.existsByChatRoomAndMemberId(room, memberId)) {
            throw new ChatException(ChatErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }
        return room;
    }

    private boolean isViewerCompanySide(ChatRoom room, Long memberId) {
        return room.getCompanyProfile().getMember().getId().equals(memberId);
    }

    private MemberType resolveSenderType(ChatRoom room, Long senderId) {
        return room.getCompanyProfile().getMember().getId().equals(senderId) ? MemberType.COMPANY : MemberType.TALENT;
    }

    private ChatRoomResDTO toRoomResDTO(ChatRoom room, Long viewerMemberId) {
        boolean viewerIsCompany = isViewerCompanySide(room, viewerMemberId);

        Long originId = room.getOriginType() == ChatOriginType.PORTFOLIO
                ? room.getPortfolio().getId()
                : room.getJobPosting().getId();
        String originTitle = room.getOriginType() == ChatOriginType.PORTFOLIO
                ? room.getPortfolio().getTitle()
                : room.getJobPosting().getTitle();

        return ChatRoomResDTO.builder()
                .chatRoomId(room.getId())
                .counterpartName(viewerIsCompany ? room.getTalentProfile().getName() : room.getCompanyProfile().getCompanyName())
                .counterpartType(viewerIsCompany ? MemberType.TALENT : MemberType.COMPANY)
                .originType(room.getOriginType())
                .originId(originId)
                .originTitle(originTitle)
                .build();
    }
}

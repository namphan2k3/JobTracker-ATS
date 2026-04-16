package com.jobtracker.jobtracker_app.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.jobtracker_app.configurations.VnPayConfig;
import com.jobtracker.jobtracker_app.dto.requests.payment.PaymentRequest;
import com.jobtracker.jobtracker_app.dto.responses.payment.InitPaymentResponse;
import com.jobtracker.jobtracker_app.dto.responses.payment.PaymentResponse;
import com.jobtracker.jobtracker_app.entities.Company;
import com.jobtracker.jobtracker_app.entities.CompanySubscription;
import com.jobtracker.jobtracker_app.entities.Payment;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.enums.PaymentStatus;
import com.jobtracker.jobtracker_app.enums.SubscriptionStatus;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.PaymentMapper;
import com.jobtracker.jobtracker_app.repositories.CompanyRepository;
import com.jobtracker.jobtracker_app.repositories.CompanySubscriptionRepository;
import com.jobtracker.jobtracker_app.repositories.PaymentRepository;
import com.jobtracker.jobtracker_app.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImpl implements PaymentService {

    PaymentRepository paymentRepository;
    CompanyRepository companyRepository;
    CompanySubscriptionRepository companySubscriptionRepository;
    PaymentMapper paymentMapper;
    VnPayConfig vnPayConfig;


    @Override
    @Transactional
    @PreAuthorize("hasAuthority('PAYMENT_CREATE')")
    public InitPaymentResponse create(PaymentRequest request, HttpServletRequest httpServletRequest)  throws UnsupportedEncodingException {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_EXISTED));

        CompanySubscription companySubscription = companySubscriptionRepository
                .findByIdAndCompany_Id(request.getCompanySubscriptionId(), request.getCompanyId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_SUBSCRIPTION_NOT_EXISTED));

        Payment payment = paymentMapper.toPayment(request);
        payment.setCompany(company);
        payment.setCompanySubscription(companySubscription);

        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        if (payment.getCurrency() == null) {
            payment.setCurrency("VND");
        }

        if (payment.getGateway() == null) {
            payment.setGateway("VNPAY");
        }

        if (payment.getTxnRef() == null) {
            payment.setTxnRef(vnPayConfig.getRandomNumber(8));
        }

        payment.setStatus(PaymentStatus.INIT);

        paymentRepository.save(payment);

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = payment.getAmount().multiply(BigDecimal.valueOf(100)).longValue();

        String code = payment.getTxnRef();

        String vnp_TxnRef = code;
        String vnp_IpAddr = vnPayConfig.getIpAddress(httpServletRequest);

        String vnp_TmnCode = vnPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = vnPayConfig.hmacSHA512(vnPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnPayConfig.vnp_PayUrl + "?" + queryUrl;

        return InitPaymentResponse.builder()
                .payment(paymentMapper.toPaymentResponse(payment))
                .paymentUrl(paymentUrl)
                .build();
    }

    @Override
    @Transactional
    public boolean paymentReturn(HttpServletRequest request) throws UnsupportedEncodingException, JsonProcessingException {
        String paymentCode = request.getParameter("vnp_TxnRef");
        Payment payment = paymentRepository.findByTxnRef(paymentCode)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        // Throw lỗi có thể làm VNPAY lỗi
        if(payment.getStatus() != PaymentStatus.INIT){
            return payment.getStatus() == PaymentStatus.SUCCESS;
        }

        Map fields = new HashMap();

        for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = null;
            String fieldValue = null;
            fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.UTF_8);
            fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.UTF_8);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        String signValue = vnPayConfig.hashAllFields(fields);
        ObjectMapper mapper = new ObjectMapper();

        if (signValue.equals(vnp_SecureHash)) {
            payment.setMetadata(mapper.writeValueAsString(fields));

            if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setPaidAt(LocalDateTime.now());

                List<CompanySubscription> activeSubscriptions = companySubscriptionRepository
                        .findByCompany_IdAndStatus(payment.getCompany().getId(), SubscriptionStatus.ACTIVE);
                for (CompanySubscription active : activeSubscriptions) {
                    active.setStatus(SubscriptionStatus.EXPIRED);
                    active.setEndDate(LocalDateTime.now());
                }

                CompanySubscription subscription = payment.getCompanySubscription();
                subscription.setStatus(SubscriptionStatus.ACTIVE);
                subscription.setStartDate(LocalDateTime.now());
                subscription.setEndDate(LocalDateTime.now().plusDays(
                        subscription.getPlan().getDurationDays()));

                activeSubscriptions.add(subscription);
                companySubscriptionRepository.saveAll(activeSubscriptions);

                return true;
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                return false;
            }
        } else {
            payment.setMetadata(mapper.writeValueAsString(fields));
            payment.setStatus(PaymentStatus.FAILED);
            return false;
        }
    }

    @Override
    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    public PaymentResponse getById(String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));
        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    public Page<PaymentResponse> getAll(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(paymentMapper::toPaymentResponse);
    }

    @Override
    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    public Page<PaymentResponse> getByCompany(String companyId, Pageable pageable) {
        return paymentRepository.findByCompany_Id(companyId, pageable)
                .map(paymentMapper::toPaymentResponse);
    }

    @Override
    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    public Page<PaymentResponse> getByCompanySubscription(String companySubscriptionId, Pageable pageable) {
        return paymentRepository.findByCompanySubscription_Id(companySubscriptionId, pageable)
                .map(paymentMapper::toPaymentResponse);
    }
}



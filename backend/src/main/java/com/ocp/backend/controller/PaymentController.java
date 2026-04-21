package com.ocp.backend.controller;

import com.ocp.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    // 送出訂單至綠界
    // POST /api/payment/submit/{orderId}
    @PostMapping("/submit/{orderId}")
    public ResponseEntity<String> submitPayment(@PathVariable Long orderId) {
        String htmlForm = paymentService.createEcpayForm(orderId);
        return ResponseEntity.ok(htmlForm);
    }

    /**
     * ECPay 付款結果回調 (Server-to-Server)
     * 綠界會在付款完成後 POST 到這個 endpoint
     */
    @PostMapping("/callback")
    public String handleCallback(@RequestParam Map<String, String> params) {
        log.info("收到綠界回條: {}", params);

        // 1. 驗證 CheckMacValue (防止偽造攻擊)
        if (!paymentService.verifyCheckMacValue(params)) {
            log.error("CheckMacValue 驗證失敗！可能是偽造的 callback");
            return "0|CheckMacValue Error";
        }

        // 2. 判斷交易結果 (RtnCode = 1 代表成功)
        String rtnCode = params.get("RtnCode");

        if ("1".equals(rtnCode)) {
            // 3. 取出訂單編號
            String merchantTradeNo = params.get("MerchantTradeNo");
            log.info("交易成功，訂單編號: {}", merchantTradeNo);

            // 4. 呼叫 Service 更新訂單狀態
            paymentService.processPaymentResult(merchantTradeNo);

            return "1|OK"; // 告訴綠界我收到了
        }

        log.warn("交易失敗或取消，RtnCode: {}", rtnCode);
        return "0|Transaction Failed";
    }
}

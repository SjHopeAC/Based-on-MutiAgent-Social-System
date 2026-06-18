package com.example.helloworld.util;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BertClient {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String BERT_URL = "http://localhost:5000";

    public static Map<String, Object> callBestMatch(String currentSummary, List<Map<String, Object>> candidates) {
        try {
            // 构建请求参数
            HashMap<String, Object> params = new HashMap<>();
            params.put("current_summary", currentSummary);
            params.put("candidates", candidates);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 发送请求
            HttpEntity<HashMap<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    BERT_URL + "/best_match",
                    request,
                    Map.class
            );

            return response.getBody();
        } catch (Exception e) {
            System.err.println("BERT接口调用失败: " + e.getMessage());
            HashMap<String, Object> empty = new HashMap<>();
            empty.put("best_match", null);
            return empty;
        }
    }

    public static Map<String, Object> callBatchSimilarity(String currentSummary, List<Map<String, Object>> candidates) {
        try {
            // 构建请求参数
            HashMap<String, Object> params = new HashMap<>();
            params.put("current_summary", currentSummary);
            params.put("candidates", candidates);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 发送请求
            HttpEntity<HashMap<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    BERT_URL + "/batch_similarity",
                    request,
                    Map.class
            );

            return response.getBody();
        } catch (Exception e) {
            System.err.println("BERT接口调用失败: " + e.getMessage());
            HashMap<String, Object> empty = new HashMap<>();
            // 用 new ArrayList<>() 替代 List.of()
            empty.put("matched_list", new ArrayList<>());
            empty.put("best_match", null);
            return empty;
        }
    }
}
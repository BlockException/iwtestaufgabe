package net.iwmedia.report.common.service;

import com.google.gson.Gson;
import net.iwmedia.report.api.dto.ReportDto;
import net.iwmedia.report.api.service.ReportPublisher;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class RedisReportPublisher implements ReportPublisher {
    private static final String NEW_CHANNEL = "reports:new";
    private static final String STATUS_CHANNEL = "reports:status_update";
    private final RedisService redisService;
    private final Gson gson = new Gson();

    public RedisReportPublisher(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public void publishNewReport(ReportDto reportDto) {
        try (Jedis jedis = redisService.getPool().getResource()) {
            Map<String, String> payload = new HashMap<>();
            payload.put("reportId", reportDto.id());
            payload.put("reporterUuid", reportDto.reporter().toString());
            payload.put("reporterName", reportDto.reporterName());
            payload.put("reportedUuid", reportDto.reported().toString());
            payload.put("reportedName", reportDto.reportedName());
            payload.put("reason", reportDto.reason());
            payload.put("server", reportDto.server());
            payload.put("timestamp", Instant.now().toString());
            jedis.publish(NEW_CHANNEL, gson.toJson(payload));
        }
    }

    @Override
    public void publishStatusUpdate(String reportId, String reporterUuid, String reporterName, String reportedName, String status, String handledBy, String modNote) {
        try (Jedis jedis = redisService.getPool().getResource()) {
            Map<String, String> payload = new HashMap<>();
            payload.put("reportId", reportId);
            payload.put("reporterUuid", reporterUuid);
            payload.put("reporterName", reporterName);
            payload.put("reportedName", reportedName);
            payload.put("status", status);
            payload.put("handledBy", handledBy);
            payload.put("modNote", modNote == null ? "" : modNote);
            payload.put("timestamp", Instant.now().toString());
            jedis.publish(STATUS_CHANNEL, gson.toJson(payload));
        }
    }
}

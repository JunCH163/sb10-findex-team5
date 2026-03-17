package com.sprint.findex.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.findex.dto.dashboard.IndexPerformanceDto;
import com.sprint.findex.entity.IndexData;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.enums.PeriodType;
import com.sprint.findex.enums.SourceType;
import com.sprint.findex.repository.IndexDataRepository;
import com.sprint.findex.repository.IndexInfoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private IndexInfoRepository indexInfoRepository;

    @Autowired
    private IndexDataRepository indexDataRepository;

    @Test
    @DisplayName("일간 수익률 조회 시 beforePrice는 closingPrice - versus 로 계산된다")
    void getFavoriteIndexPerformance_daily_calculatesBeforePrice() {
        IndexInfo indexInfo = indexInfoRepository.save(createFavoriteIndexInfo("KOSPI"));
        indexDataRepository.save(createIndexData(
                indexInfo,
                LocalDate.of(2026, 3, 17),
                new BigDecimal("110.0000"),
                new BigDecimal("10.0000")
        ));

        List<IndexPerformanceDto> result = dashboardService.getFavoriteIndexPerformance(PeriodType.DAILY);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).beforePrice()).isEqualByComparingTo("100.0000");
        assertThat(result.get(0).versus()).isEqualByComparingTo("10.0000");
        assertThat(result.get(0).fluctuationRate()).isEqualByComparingTo("10.00");
    }

    private IndexInfo createFavoriteIndexInfo(String indexName) {
        return IndexInfo.create(
                indexName,
                "국내",
                100,
                LocalDate.of(2024, 1, 1),
                new BigDecimal("1000.0000"),
                SourceType.USER,
                true
        );
    }

    private IndexData createIndexData(
            IndexInfo indexInfo,
            LocalDate baseDate,
            BigDecimal closingPrice,
            BigDecimal versus
    ) {
        return IndexData.create(
                indexInfo,
                baseDate,
                SourceType.USER,
                closingPrice,
                closingPrice,
                closingPrice,
                closingPrice,
                versus,
                BigDecimal.ZERO,
                0L,
                0L,
                0L
        );
    }
}

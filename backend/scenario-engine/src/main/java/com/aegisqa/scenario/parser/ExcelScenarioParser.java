package com.aegisqa.scenario.parser;

import com.aegisqa.domain.scenario.Scenario;
import com.aegisqa.domain.scenario.ScenarioSource;
import com.aegisqa.domain.scenario.ScenarioStep;
import com.aegisqa.scenario.rules.ParserRuleRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Parses Excel (.xlsx) test case sheets.
 *
 * Expected columns: Test Case ID | Scenario Description | Expected Result
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelScenarioParser implements ScenarioParser {

    private final ParserRuleRegistry ruleRegistry;

    @Override
    public Scenario parse(String content, String name) {
        // content is base64-encoded Excel bytes
        log.debug("Parsing Excel scenario: {}", name);
        try {
            byte[] excelBytes = Base64.getDecoder().decode(content.trim());
            return parseBytes(excelBytes, name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Excel content must be base64-encoded", e);
        }
    }

    public Scenario parseBytes(byte[] excelBytes, String name) {
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheetAt(0);
            List<ScenarioStep> steps = new ArrayList<>();
            int stepNum = 1;

            // skip header row (row 0)
            for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) continue;

                String scenarioText = getCellString(row, 1);
                String expectedText = getCellString(row, 2);

                if (scenarioText == null || scenarioText.isBlank()) continue;

                // Split multi-line scenario description
                String[] scenarioLines = scenarioText.split("[\\r\\n]+");
                for (String line : scenarioLines) {
                    line = line.trim();
                    if (line.isBlank()) continue;
                    ScenarioStep step = ruleRegistry.matchLine(line, stepNum);
                    if (step == null) {
                        step = ScenarioStep.builder()
                                .stepNumber(stepNum)
                                .rawText(line)
                                .actionType("unknown")
                                .build();
                    }
                    steps.add(step);
                    stepNum++;
                }
            }

            return Scenario.builder()
                    .id(UUID.randomUUID().toString())
                    .name(name)
                    .source(ScenarioSource.EXCEL)
                    .steps(steps)
                    .build();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to parse Excel file: " + e.getMessage(), e);
        }
    }

    private String getCellString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((long) cell.getNumericCellValue());
        return null;
    }

    @Override
    public ScenarioSource getSupportedSource() {
        return ScenarioSource.EXCEL;
    }
}

package com.redhat.springinitializr.api;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import org.apache.commons.lang3.StringUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.redhat.springinitializr.model.ProjectGenerationStatistic;
import com.redhat.springinitializr.model.introspector.ProjectGenerationStatisticIntrospector;

public class StatsExcelView extends AbstractXlsxView {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a z");

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) {
		var stats = Optional.ofNullable((Collection<ProjectGenerationStatistic>) model.get("stats")).orElseGet(Collections::emptyList);
		var rowNum = new AtomicInteger(0);
		var sheet = workbook.createSheet("Stats");
		var fields = createHeader(stats, rowNum, workbook, sheet, response);

		createBody(stats, rowNum, workbook, sheet, fields);
	}

	private static void createBody(Collection<ProjectGenerationStatistic> stats, AtomicInteger rowNum, Workbook workbook, Sheet sheet, Collection<String> fields) {
		var dateStyle = getDateStyle(workbook);
		var cellNum = new AtomicInteger(0);

		stats.forEach(stat -> {
			cellNum.set(0);
			var row = sheet.createRow(rowNum.getAndIncrement());

			fields.forEach(field -> setCellValue(field, stat, row.createCell(cellNum.getAndIncrement()), dateStyle));
		});
	}

	private static Collection<String> createHeader(Collection<ProjectGenerationStatistic> stats, AtomicInteger rowNum, Workbook workbook, Sheet sheet, HttpServletResponse response) {
		var boldStyle = getBoldFont(workbook);
		var dateTimeRightNow = ZonedDateTime.now().format(DATE_FORMATTER);
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"Red Hat Spring Initializr Stats - %s.xlsx\"", dateTimeRightNow));
		var titleCell = sheet.createRow(rowNum.getAndIncrement()).createCell(0);
		titleCell.setCellValue(String.format("Spring Initializr Stats as of %s", dateTimeRightNow));
		titleCell.setCellStyle(boldStyle);
		titleCell.setCellType(CellType.STRING);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

		var numRecordsCell = sheet.createRow(rowNum.getAndUpdate(i -> i + 2)).createCell(0);
		numRecordsCell.setCellValue(String.format("%d records", stats.size()));
		numRecordsCell.setCellStyle(boldStyle);
		numRecordsCell.setCellType(CellType.STRING);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

		var header = sheet.createRow(rowNum.getAndIncrement());
		sheet.createFreezePane(0, header.getRowNum() + 1);

		var fields = ProjectGenerationStatisticIntrospector.getInstance().getPropertyNames();
		var cellNum = new AtomicInteger(0);

		fields.forEach(field -> {
			var headerCell = header.createCell(cellNum.getAndIncrement());
			headerCell.setCellStyle(boldStyle);
			headerCell.setCellType(CellType.STRING);
			headerCell.setCellValue(splitCamelCase(field));
			headerCell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
		});

		return fields;
	}

	private static void setCellValue(String field, ProjectGenerationStatistic statistic, Cell cell, CellStyle dateStyle) {
		ProjectGenerationStatisticIntrospector.getInstance().getValue(statistic, field).ifPresent(fieldValue -> {
			var fieldValueType = fieldValue.getClass();

			if (Boolean.TYPE.isAssignableFrom(fieldValueType) || Boolean.class.isAssignableFrom(fieldValueType)) {
				cell.setCellValue((Boolean) fieldValue);
				cell.setCellType(CellType.BOOLEAN);
			}
			else if (Calendar.class.isAssignableFrom(fieldValueType)) {
				cell.setCellValue((Calendar) fieldValue);
				cell.setCellStyle(dateStyle);
			}
			else if (Date.class.isAssignableFrom(fieldValueType)) {
				cell.setCellValue((Date) fieldValue);
				cell.setCellStyle(dateStyle);
			}
			else if (Double.TYPE.isAssignableFrom(fieldValueType) || Double.class.isAssignableFrom(fieldValueType)) {
				cell.setCellValue((Double) fieldValue);
				cell.setCellType(CellType.NUMERIC);
			}
			else if (String.class.isAssignableFrom(fieldValueType)) {
				cell.setCellValue((String) fieldValue);
				cell.setCellType(CellType.STRING);
			}
			else if (Number.class.isAssignableFrom(fieldValueType) || Integer.TYPE.isAssignableFrom(fieldValueType) || Short.TYPE.isAssignableFrom(fieldValueType) || Long.TYPE.isAssignableFrom(fieldValueType) || Float.TYPE.isAssignableFrom(fieldValueType)) {
				cell.setCellValue(String.valueOf((Number) fieldValue));
				cell.setCellType(CellType.NUMERIC);
			}
			else {
				cell.setCellValue(fieldValue.toString());
				cell.setCellType(CellType.STRING);
			}
		});
	}

	private static CellStyle getBoldFont(Workbook workbook) {
		var boldStyle = workbook.createCellStyle();
		var boldFont = workbook.createFont();
		boldFont.setBold(true);

		return boldStyle;
	}

	private static CellStyle getDateStyle(Workbook workbook) {
		var cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss.SSS"));

		return cellStyle;
	}

	private static String splitCamelCase(String string) {
		return StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(string), StringUtils.SPACE));
	}
}

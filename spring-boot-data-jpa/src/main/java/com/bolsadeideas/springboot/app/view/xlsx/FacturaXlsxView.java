package com.bolsadeideas.springboot.app.view.xlsx;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.bolsadeideas.springboot.app.models.entity.Factura;
import com.bolsadeideas.springboot.app.models.entity.ItemFactura;

@Component("factura/ver.xlsx")
public class FacturaXlsxView extends AbstractXlsxView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		MessageSourceAccessor mensajes = getMessageSourceAccessor();
		
		Factura factura = (Factura) model.get("factura");
		Sheet sheet = workbook.createSheet(mensajes.getMessage("text.invoice.name") + " Spring");
		
		sheet.createRow(0).createCell(0).setCellValue(mensajes.getMessage("text.cliente.detail"));
		sheet.createRow(1).createCell(0).setCellValue(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
		sheet.createRow(2).createCell(0).setCellValue(factura.getCliente().getEmail());
		sheet.createRow(4).createCell(0).setCellValue(mensajes.getMessage("text.invoice.detail"));
		sheet.createRow(5).createCell(0).setCellValue(mensajes.getMessage("text.invoice.sheet") + ": " + factura.getId());
		sheet.createRow(6).createCell(0).setCellValue(mensajes.getMessage("text.invoice.description") + ": " + factura.getDescripcion());
		sheet.createRow(7).createCell(0).setCellValue(mensajes.getMessage("text.invoice.date") + ": " + factura.getCreateAt());
		
		Row header = sheet.createRow(9);
		header.createCell(0).setCellValue(mensajes.getMessage("text.invoice.product"));
		header.createCell(1).setCellValue(mensajes.getMessage("text.invoice.price"));
		header.createCell(2).setCellValue(mensajes.getMessage("text.invoice.quantity"));
		header.createCell(3).setCellValue(mensajes.getMessage("text.invoice.total"));
		
		int rownum = 10;
		
		for (ItemFactura item : factura.getItems()) {
			
			Row fila = sheet.createRow(rownum++);
			fila.createCell(0).setCellValue(item.getProducto().getNombre());
			fila.createCell(1).setCellValue(item.getProducto().getPrecio());
			fila.createCell(2).setCellValue(item.getCantidad());
			fila.createCell(1).setCellValue(item.calcularImporte());
			
		}
		
		Row filatotal = sheet.createRow(rownum);
		filatotal.createCell(2).setCellValue(mensajes.getMessage("text.invoice.total"));
		filatotal.createCell(3).setCellValue(factura.getTotal());
		
	}
	
}

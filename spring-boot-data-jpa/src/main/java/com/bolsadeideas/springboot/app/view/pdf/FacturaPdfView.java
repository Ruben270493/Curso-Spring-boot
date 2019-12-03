package com.bolsadeideas.springboot.app.view.pdf;

import java.awt.Color;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.bolsadeideas.springboot.app.models.entity.Factura;
import com.bolsadeideas.springboot.app.models.entity.ItemFactura;
import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Component("factura/ver")
public class FacturaPdfView extends AbstractPdfView {
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private LocaleResolver localeResolver;

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		MessageSourceAccessor mensajes = getMessageSourceAccessor();
		
		Factura factura = (Factura) model.get("factura");
		PdfPTable tabla = new PdfPTable(1);
		
		PdfPCell cell = new PdfPCell(new Phrase(mensajes.getMessage("text.cliente.detail")));
		
		cell.setBackgroundColor(new Color(184, 218, 255));
		cell.setPadding(8f);
		
		tabla.setSpacingAfter(20);
		tabla.addCell(cell);
		tabla.addCell(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
		tabla.addCell(factura.getCliente().getEmail());
		
		PdfPTable tabla2 = new PdfPTable(1);
		
		cell = new PdfPCell(new Phrase(mensajes.getMessage("text.invoice.detail")));
		
		cell.setBackgroundColor(new Color(195, 230, 203));
		cell.setPadding(8f);
		
		tabla2.setSpacingAfter(20);
		tabla2.addCell(cell);
		tabla2.addCell(mensajes.getMessage("text.invoice.sheet") + ": " + factura.getId());
		tabla2.addCell(mensajes.getMessage("text.invoice.description") + ": " + factura.getDescripcion());
		tabla2.addCell(mensajes.getMessage("text.invoice.date") + ": " + factura.getCreateAt());
		
		document.add(tabla);
		document.add(tabla2);
		
		PdfPTable tabla3 = new PdfPTable(4);
		
		tabla3.setWidths(new float [] {3.5f, 1, 1, 1});
		tabla3.addCell(mensajes.getMessage("text.invoice.product"));
		tabla3.addCell(mensajes.getMessage("text.invoice.price"));
		tabla3.addCell(mensajes.getMessage("text.invoice.quantity"));
		tabla3.addCell(mensajes.getMessage("text.invoice.total"));
		
		for (ItemFactura item: factura.getItems()) {
			
			tabla3.addCell(item.getProducto().getNombre());
			tabla3.addCell(item.getProducto().getPrecio().toString());
			
			cell = new PdfPCell(new Phrase(item.getCantidad().toString()));
			cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			
			tabla3.addCell(cell);
			tabla3.addCell(item.calcularImporte().toString());
			
		}
		
		cell = new PdfPCell(new Phrase(mensajes.getMessage("text.invoice.total") + ": "));
		cell.setColspan(3);
		cell.setHorizontalAlignment(PdfCell.ALIGN_RIGHT);
		tabla3.addCell(cell);
		tabla3.addCell(factura.getTotal().toString());
		
		document.add(tabla3);
		
	}

}

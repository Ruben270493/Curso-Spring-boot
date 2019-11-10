$(document).ready(function() {
	
	$('#buscar_producto').autocomplete({
		source: function(request, response) {
			$.ajax({
				url: "/factura/cargar-productos/" + request.term,
				dataType: "json",
				data: {
					term: request.term
				},
				success: function(data) {
					response($.map(data, function(item) {
						return {
							value: item.id,
							label: item.nombre,
							precio: item.precio,
						};
					}));
				},
			});
		},
		select: function(event, ui) {
			//$("#buscar_producto").val(ui.item.label);
			var linea = $("#plantillaItemsFactura").html();
			
			linea = linea.replace(/{ID}/g, ui.item.value);
			linea = linea.replace(/{NOMBRE}/g, ui.item.label);
			linea = linea.replace(/{PRECIO}/g, ui.item.precio);
			
			$("#cargarItemsProductos tbody").append(linea);
			itemsHelper.calcularImporte(ui.item.value, ui.item.precio, 1);
			
			return false;
		}
	});
	
	var itemsHelper = {
			calcularImporte: function(id, precio, cantidad) {
				$("#total_importe_" + id).html(parseInt(precio) * parseInt(cantidad));
			} 
	}
	
});
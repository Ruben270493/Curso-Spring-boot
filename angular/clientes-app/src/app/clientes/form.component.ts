import { Component, OnInit } from '@angular/core';
import { Cliente } from './cliente';
import { ClienteService } from './cliente.service';
import { Router, ActivatedRoute } from '@angular/router';
import swal from 'sweetalert2';

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class FormComponent implements OnInit {

  private cliente:Cliente = new Cliente();
  private title:string = "Crear cliente";

  constructor(private clienteService:ClienteService, private router:Router, private activatedRoute:ActivatedRoute) { }

  ngOnInit() {
    this.cargarCliente();
  }

  public cargarCliente():void {
    this.activatedRoute.params.subscribe(params => {
      let id = params['id']
      if (id) {
        this.clienteService.getCliente(id).subscribe(
          (cliente) => this.cliente = cliente
        )
      }
    });
  }

  public create():void {
    this.clienteService.create(this.cliente).subscribe(
      cliente => {
        this.router.navigate(['/clientes'])
        swal.fire('Nuevo cliente', `¡Cliente ${cliente.nombre} creado correctamente!`, 'success')
      }
    );
  }

  public update():void {
    this.clienteService.update(this.cliente).subscribe(json => {
      this.router.navigate(['/clientes'])
      swal.fire('Cliente actualizado', `El cliente ${json.cliente.nombre} se ha actualizado correctamente`, 'success')
    });
  }

}

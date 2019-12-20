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
  private errores:string[];

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
      },
      err => {
        this.errores = err.error.errors as string[];
        console.error("Código de error: " + err.status);
        console.error(err.error.errors);
      }
    );
  }

  public update():void {
    this.clienteService.update(this.cliente).subscribe(
      json => {
        this.router.navigate(['/clientes'])
        swal.fire('Cliente actualizado', `El cliente ${json.cliente.nombre} se ha actualizado correctamente`, 'success')
      },
      err => {
        this.errores = err.error.errors as string[];
        console.error("Código de error: " + err.status);
        console.error(err.error.errors);
      }
    );
  }

}

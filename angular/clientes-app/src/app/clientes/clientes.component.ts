import { Component, OnInit } from '@angular/core';
import { Cliente } from './cliente';
import { ClienteService } from './cliente.service';
import swal from 'sweetalert2';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'app-clientes',
  templateUrl: './clientes.component.html'
})
export class ClientesComponent implements OnInit {

  clientes:Cliente[];

  constructor(private clienteService:ClienteService) { }

  ngOnInit() {
    this.clienteService.getClientes().pipe(
      tap(clientes => {
        console.log('ClientesComponent:tap 3');
        clientes.forEach(
          cliente => {
            console.log(cliente.nombre);
          });
      })
    ).subscribe(clientes => this.clientes = clientes);
  }

  public delete(cliente:Cliente):void {

      const swalWithBootstrapButtons = swal.mixin({

        customClass: {
          confirmButton: 'btn btn-success',
          cancelButton: 'btn btn-danger'
        },

        buttonsStyling: false

      })

      swalWithBootstrapButtons.fire({

        title: '¿Estás seguro?',
        text: `¿Estás seguro de que quieres eliminar el cliente ${cliente.nombre}?`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'No, cancelar!',
        reverseButtons: true

      }).then((result) => {

        if (result.value) {
          this.clienteService.delete(cliente.id).subscribe(
            response => {
              this.clientes = this.clientes.filter(cli => cli !== cliente)
              swalWithBootstrapButtons.fire('¡Eliminado!',`El cliente ${cliente.nombre} ha sido eliminado correctamente`,'success')
            }
          )
        }

      })

    }

}

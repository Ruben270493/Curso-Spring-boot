import { Component, OnInit } from '@angular/core';
import { Cliente } from './cliente';

@Component({
  selector: 'app-clientes',
  templateUrl: './clientes.component.html'
})
export class ClientesComponent implements OnInit {

  clientes:Cliente[] = [
    {id: 1, nombre: 'Rubén', apellido: 'Fernández de Castro', email: 'ruben@curso.com', createAt: '2019-12-15'},
    {id: 2, nombre: 'Andrés', apellido: 'Guzman', email: 'andres@curso.com', createAt: '2018-12-15'},
    {id: 3, nombre: 'Sonia', apellido: 'Sanchez', email: 'sonia@curso.com', createAt: '2016-12-15'},
    {id: 2, nombre: 'Trang', apellido: 'Bu', email: 'trang@curso.com', createAt: '2019-12-15'}
  ];

  constructor() { }

  ngOnInit() {
  }

}

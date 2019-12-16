import { Injectable } from '@angular/core';
import { Cliente } from './cliente';
import { CLIENTES } from './clientes.json';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';

@Injectable()
export class ClienteService {

  constructor() { }

  getClientes():Observable<Cliente[]> {
    return of(CLIENTES);
  }

}

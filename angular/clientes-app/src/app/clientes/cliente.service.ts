import { Injectable } from '@angular/core';
import { Cliente } from './cliente';
import { CLIENTES } from './clientes.json';
import { Observable, of, from, throwError } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import swal from 'sweetalert2';
import { Router } from '@angular/router';

@Injectable()
export class ClienteService {

  private urlEndPoint:string = 'http://localhost:8080/api/clientes/';
  private httpHeaders = new HttpHeaders({'Content-Type':'application/json'})

  constructor(private http:HttpClient, private router:Router) { }

  public getClientes():Observable<Cliente[]> {
    //return of(CLIENTES);
    return this.http.get<Cliente[]>(this.urlEndPoint);
  }

  public create(cliente:Cliente):Observable<Cliente> {
    return this.http.post<Cliente>(this.urlEndPoint, cliente, {headers: this.httpHeaders});
  }

  public getCliente(id):Observable<Cliente> {
    return this.http.get<Cliente>(`${this.urlEndPoint}${id}`).pipe(
      catchError(e => {
        this.router.navigate(['/clientes']);
        swal.fire('Error al editar', e.error.mensaje, 'error');
        return throwError(e);
      })
    );
  }

  public update(cliente:Cliente):Observable<Cliente> {
    return this.http.put<Cliente>(`${this.urlEndPoint}${cliente.id}`, cliente, {headers: this.httpHeaders});
  }

  public delete(id:number):Observable<Cliente> {
    return this.http.delete<Cliente>(`${this.urlEndPoint}${id}`, {headers: this.httpHeaders})
  }

}

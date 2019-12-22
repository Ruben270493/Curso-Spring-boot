import { Injectable } from '@angular/core';
import { formatDate, DatePipe } from '@angular/common';
import localeES from '@angular/common/locales/es';
import { Cliente } from './cliente';
import { CLIENTES } from './clientes.json';
import { Observable, of, from, throwError } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import swal from 'sweetalert2';
import { Router } from '@angular/router';

@Injectable()
export class ClienteService {

  private urlEndPoint:string = 'http://localhost:8080/api/clientes/';
  private httpHeaders = new HttpHeaders({'Content-Type':'application/json'})

  constructor(private http:HttpClient, private router:Router) { }

  public getClientes():Observable<Cliente[]> {
    //return of(CLIENTES);
    return this.http.get<Cliente[]>(this.urlEndPoint).pipe(
      tap(response => {
        let clientes = response as Cliente[];
        console.log('ClientesService:tap 1');
        clientes.forEach(
          cliente => {
            console.log(cliente.nombre);
          }
        )
      }),
      map(response => {
        let clientes = response as Cliente[];
        return clientes.map(cliente => {
            cliente.nombre = cliente.nombre.toUpperCase();
            let datePipe = new DatePipe('es');
            //cliente.createAt = datePipe.transform(cliente.createAt, 'fullDate')
            //formatDate(cliente.createAt, 'dd-MM-yyyy', 'en-US');
            return cliente;
        })
      }),
      tap(response => {
        console.log('ClientesService:tap 2');
        response.forEach(
          cliente => {
            console.log(cliente.nombre);
          }
        )
      }),
    );
  }

  public create(cliente:Cliente):Observable<Cliente> {
    return this.http.post(this.urlEndPoint, cliente, {headers: this.httpHeaders}).pipe(
      map((response:any) => response.cliente as Cliente),
      catchError(e => {

        if (e.status == 400)
            return throwError(e);

        console.error(e.error.mensaje);
        swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  public getCliente(id):Observable<Cliente> {
    return this.http.get<Cliente>(`${this.urlEndPoint}${id}`).pipe(
      catchError(e => {
        this.router.navigate(['/clientes']);
        swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  public update(cliente:Cliente):Observable<any> {
    return this.http.put<any>(`${this.urlEndPoint}${cliente.id}`, cliente, {headers: this.httpHeaders}).pipe(
      catchError(e => {

        if (e.status == 400)
            return throwError(e);

        console.error(e.error.mensaje);
        swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  public delete(id:number):Observable<Cliente> {
    return this.http.delete<Cliente>(`${this.urlEndPoint}${id}`, {headers: this.httpHeaders}).pipe(
      catchError(e => {
        console.error(e.error.mensaje);
        swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    )
  }

}

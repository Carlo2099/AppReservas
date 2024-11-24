package com.example.reservasimei.Clases

class Reservaciones {
    var id: Int = 0
    var userId: Int = 0
    var horas: Int = 0
    var fechaReserva: String = ""
    var horaReserva: String = ""
    var pago: String = ""
    var nombreCampo: String = ""
    var tipoCampo: String = ""
    var precioHora: Double = 0.0
    var total: Double = 0.0

    constructor(userId: Int, horas: Int, fechaReserva: String, horaReserva: String, pago: String, nombreCampo: String, tipoCampo: String, precioHora: Double, total: Double) {
        this.userId = userId
        this.horas = horas
        this.fechaReserva = fechaReserva
        this.horaReserva = horaReserva
        this.pago = pago
        this.nombreCampo = nombreCampo
        this.tipoCampo = tipoCampo
        this.precioHora = precioHora
        this.total = total
    }

    constructor() {

    }
}

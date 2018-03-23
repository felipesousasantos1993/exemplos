 baixarQuadroFipSusep(objExport, tipo, mes, ano, quadro) {
        var url = environment.baseExportarFipsusep + this.endPointExportarFipSusepExport14C;
        url += this.service.getTokenZup();
        url += "&tipo=" + tipo;
        url += "&mes=" + mes;
        url += "&ano=" + ano;
        url += "&quadro=" + quadro;

        var contentType = tipo == 'PDF' ? 'application/pdf' : 'vnd.ms-excel';

        return this.http.post(url, objExport, { headers: this.headers, responseType: ResponseContentType.Blob })
            .map(
            (res) => {
                return new Blob([res.blob()], { type: contentType })
            })
    }

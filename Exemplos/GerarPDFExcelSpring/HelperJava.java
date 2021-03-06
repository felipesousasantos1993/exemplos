public <T> void gerarRelatorio(List<T> lista, String reportUrl, Map<String, Object> params, TipoRelEnum tipoRel, HttpServletResponse response)
			throws Exception {

	try {

		InputStream jasperStream = context.getResource(reportUrl).getInputStream();
		InputStream image = context.getResource(Constantes.RELATORIOS.LOGO_BRASILCAP).getInputStream();

		params.put(Constantes.RELATORIOS_PARAM.PARAM_IMAGE, image);
		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JRBeanCollectionDataSource(lista));

		response.setContentType(tipoRel.getContentType());
		response.setHeader("Content-disposition", "inline; filename=report" + tipoRel.getExtensao());

		final OutputStream outStream = response.getOutputStream();
		if (tipoRel.isPDF()) {
			JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
		} else if (tipoRel.isXLS()) {
			JRXlsExporter exporter = getCommonXlsExporter();

			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));
			exporter.exportReport();

		}
		response.getOutputStream().flush();
		response.getOutputStream().close();

	} catch (Exception e) {
		LOG.error(Constantes.ERRO_MSG.ERRO_EXPORTA_FIPSUSEP, e);
		throw new Exception(Constantes.ERRO_MSG.ERRO_EXPORTA_FIPSUSEP, e);
	}
}
private static JRXlsExporter getCommonXlsExporter() {
	JRXlsExporter exporter = new JRXlsExporter();
	SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
	configuration.setIgnorePageMargins(true);
	configuration.setOnePagePerSheet(false);
	configuration.setWhitePageBackground(false);
	configuration.setRemoveEmptySpaceBetweenRows(true);
	configuration.setDetectCellType(true);

	exporter.setConfiguration(configuration);

	return exporter;
}

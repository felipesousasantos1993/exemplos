package br.com.petrobras.sime.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JExcelApiExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.petrobras.fcorp.common.exception.BusinessException;
import br.com.petrobras.sime.entity.TipoRelEnum;

/**
 * @author felipe.ssantos
 * @version 1.0
 */
public abstract class GerarRelatorioUtil implements Serializable {

	private static final long serialVersionUID = 4288913089814146972L;

	private static final Logger LOGGER = LoggerFactory.getLogger(GerarRelatorioUtil.class);
	private static final String WEBINF_LOCAL_RELATIVE_PATH = "/WEB-INF/jasper/";

	public static <T> void gerarRelatorio(List<T> lista, String reportUrl, Map<String, Object> params, TipoRelEnum tipo) throws BusinessException,
			MalformedURLException {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
		String nomeArquivo = reportUrl.substring(reportUrl.lastIndexOf('/') + 1, reportUrl.lastIndexOf('.'));

		InputStream reportStream = externalContext.getResourceAsStream(WEBINF_LOCAL_RELATIVE_PATH + nomeArquivo + ".jasper");
		params.put("SUBREPORT_DIR", externalContext.getResource(WEBINF_LOCAL_RELATIVE_PATH).toString());
		try {
			byte[] bytes = null;
			final JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, params, new JRBeanCollectionDataSource(lista));
			final ByteArrayOutputStream output = new ByteArrayOutputStream();

			if (tipo.equals(TipoRelEnum.PDF)) {
				JasperExportManager.exportReportToPdfStream(jasperPrint, output);
			} else if (tipo.equals(TipoRelEnum.EXCEL)) {
				JRXlsExporter exporter = getCommonXlsExporter();

				exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, output);

				exporter.exportReport();
			}

			bytes = output.toByteArray();
			response.setContentType(tipo.getContentType());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + tipo.getExtensao() + "\"");
			response.setContentLength(bytes.length);

			final ServletOutputStream ouputStream = response.getOutputStream();
			ouputStream.write(bytes, 0, bytes.length);
			ouputStream.flush();
			ouputStream.close();
			context.responseComplete();
			context.renderResponse();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
			throw new BusinessException("projecaoEstoque.erro.gerar.excel");
		}
	}

	private static JRXlsExporter getCommonXlsExporter() {
		JRXlsExporter exporter = new JRXlsExporter();
		exporter.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
		exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
		exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
		exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
		exporter.setParameter(JExcelApiExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
		exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);

		return exporter;
	}
}

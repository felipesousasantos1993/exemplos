@RequestMapping(value = "/fip-susep-exportar", method = RequestMethod.POST)
	public ResponseEntity<?> getPDFFipSusep(@QueryParam("tipo") String tipo, @QueryParam("mes") String mes, @QueryParam("ano") String ano,
			@QueryParam("quadro") String quadro, HttpServletResponse response, @RequestBody RequestDTO body) {
		QuadrosFipsusepEnum quadroEnum = QuadrosFipsusepEnum.getPorCodigo(quadro);
		List<QuadroFipsusepDTO> listaQuadro = QuadrosFipSusepHelper.montarQuadroRelatorio(body.getResultado(), quadroEnum);

		Map<String, Object> params = new HashMap<>();
		params.put(Constantes.RELATORIOS_PARAM.PARAM_MES, mes);
		params.put(Constantes.RELATORIOS_PARAM.PARAM_ANO, ano);

		try {
			gerarRelatorioHelper.gerarRelatorio(listaQuadro, quadroEnum.getPathRelatorio(), params, TipoRelEnum.getPorDescricao(tipo), response);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<MensagemDTO>(new MensagemDTO(500, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

package br.com.precocerto.precocertoapp.bo;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import br.com.precocerto.precocertoapp.model.ProdutoLista;

public class ParserCupomFiscal {
    private final Pattern PATTERN_CODIGO_DE_BARRAS = Pattern.compile(".*([0-9ilobu]{13,13}).*", Pattern.CASE_INSENSITIVE);
    private final Pattern PATTERN_NORMALIZA_LETRAS = Pattern.compile("([a-z,])", Pattern.CASE_INSENSITIVE);
    private final Pattern PATTERN_DECIMAIS = Pattern.compile("([\\dilobu]+[.,]+[\\dilobu]+)", Pattern.CASE_INSENSITIVE);
    private final Pattern PATTERN_QUANTIDADE = Pattern.compile("^[\\s]*([0-9iolbu]+)[\\sa-z]", Pattern.CASE_INSENSITIVE);
    private final Pattern PATTERN_UNIDADE_KG = Pattern.compile("(kg)", Pattern.CASE_INSENSITIVE);
    private final Pattern PATTERN_QUANTIDADE_KG = Pattern.compile("^([\\dilobu]+[.,]+[\\dilobu]+)", Pattern.CASE_INSENSITIVE);
    private List<String> itemCupom = new ArrayList<>();
    private final static Map<String, String> MAP_LETRA_NUMERO = new HashMap<>();

    static {
        MAP_LETRA_NUMERO.put("i", "1");
        MAP_LETRA_NUMERO.put("I", "1");
        MAP_LETRA_NUMERO.put("l", "1");
        MAP_LETRA_NUMERO.put("L", "1");
        MAP_LETRA_NUMERO.put("o", "0");
        MAP_LETRA_NUMERO.put("O", "0");
        MAP_LETRA_NUMERO.put("b", "6");
        MAP_LETRA_NUMERO.put("u", "0");
        MAP_LETRA_NUMERO.put("U", "0");
        MAP_LETRA_NUMERO.put(",", ".");
    }


    public List<ProdutoLista> parseListaDeProdutos(List<CupomPosicaoOCR> listaOCR) {
        List<ProdutoLista> listaProdutosCupom = new ArrayList<>();

        trataListaOCR(listaOCR);

        if (!itemCupom.isEmpty()) {
            for (int i = 0; i < itemCupom.size(); i++) {
                ProdutoLista produto = new ProdutoLista();
                produto.setCodigoDeBarras(pegaCodigoDeBarras(itemCupom.get(i)));
                produto.setNome(pegaNomeProduto(itemCupom.get(i), produto.getCodigoDeBarras()));
                produto.setQuantidade(pegaQuantidade(itemCupom.get(i)));
                DecimaisCupom decimais = pegaValorUnitarioETotal(itemCupom.get(i), produto.getQuantidade());
                produto.setValorUnitario(decimais.getValorUnitario());
                produto.setValorTotal(decimais.getValorTotal());
                listaProdutosCupom.add(produto);
            }
        }

        return listaProdutosCupom;
    }

    private void trataListaOCR(List<CupomPosicaoOCR> listaOCR) {
        /*VERIFICAR E REMOVER ESTE PATTERN DAQUI*/
        Pattern PATTERN = Pattern.compile(".*([0-9iloub]{3,13})", Pattern.CASE_INSENSITIVE);
        Pattern PATTERN0 = Pattern.compile("RS$", Pattern.CASE_INSENSITIVE);
        Pattern PATTERN2 = Pattern.compile("[a-z]{3}", Pattern.CASE_INSENSITIVE);

        StringBuilder produtoItem = new StringBuilder();

        Collections.sort(listaOCR);
        try {
            for (int i = 0; i < listaOCR.size(); i++) {
                if (listaOCR.get(i).getTexto() != null) {

                    if (PATTERN.matcher(listaOCR.get(i).getTexto()).find()
                            && !PATTERN0.matcher(listaOCR.get(i).getTexto()).find()) {
                        produtoItem.append(listaOCR.get(i).getTexto());
                        listaOCR.get(i).setTexto(null);
                        for (int j = i + 1; j <= listaOCR.size(); j++) {
                            if (PATTERN_DECIMAIS.matcher(listaOCR.get(j).getTexto()).find()) {
                                produtoItem.append("\n" + listaOCR.get(j).getTexto());
                                listaOCR.get(j).setTexto(null);
                                if (j == listaOCR.size() - 1) {
                                    itemCupom.add(produtoItem.toString());
                                    produtoItem.delete(0, produtoItem.length());
                                    break;
                                }
                            } else if (!PATTERN.matcher(listaOCR.get(j).getTexto()).find()
                                    && PATTERN2.matcher(listaOCR.get(j).getTexto()).find()) {
                                produtoItem.append(listaOCR.get(j).getTexto());
                                listaOCR.get(j).setTexto(null);
                                if (j == listaOCR.size() - 1) {
                                    itemCupom.add(produtoItem.toString());
                                    produtoItem.delete(0, produtoItem.length());
                                    break;
                                }
                            } else {
                                itemCupom.add(produtoItem.toString());
                                produtoItem.delete(0, produtoItem.length());
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            itemCupom.clear();
        }
    }

    private boolean isItemValido(String texto) {
        Pattern PATTERN_CODIGO_DE_BARRAS = Pattern.compile(".*([0-9iloub]{13,13}).*", Pattern.CASE_INSENSITIVE);
        Matcher matcherCodigo = PATTERN_CODIGO_DE_BARRAS.matcher(texto);
        boolean resp0 = matcherCodigo.find();
        if (resp0) {
            return true;
        }

        return false;
    }

    private String pegaCodigoDeBarras(String texto) {
        Matcher matcher = PATTERN_CODIGO_DE_BARRAS.matcher(texto);

        if (matcher.find()) {
            String codigoDeBarras = (matcher.group(1));
            return normalizaLetras(codigoDeBarras);
        }
        return "0000000000000";
    }

    private String pegaNomeProduto(String texto, String codigoDeBarras) {
        try {
            int posicaoInicio = texto.lastIndexOf(codigoDeBarras) + codigoDeBarras.length();
            int posicaoFim = texto.indexOf("\n");
            String nomeProduto = texto.substring(posicaoInicio, posicaoFim).trim();
            return nomeProduto;
        } catch (IndexOutOfBoundsException e) {
            return texto;
        }
    }

    private DecimaisCupom pegaValorUnitarioETotal(String texto, int quantidade) {
        DecimaisCupom decimaisCupom = new DecimaisCupom(Double.valueOf(0), Double.valueOf(0));
        List<Double> stringDecimais = new ArrayList<>();
        String valorUnitarioETotal = texto.substring(texto.indexOf("\n") + 1, texto.length());

        Matcher matcher = PATTERN_DECIMAIS.matcher(valorUnitarioETotal);

        while (matcher.find()) {
            String valor = matcher.group(0);
            valor = normalizaLetras(valor);
            stringDecimais.add(devolveDouble(valor));
        }

        if (!isUnidadeKG(valorUnitarioETotal)) {
            if (stringDecimais.size() == 3 && quantidade > 0) {
                if ((stringDecimais.get(0) * quantidade) == stringDecimais.get(2)) {
                    decimaisCupom.setValorUnitario(stringDecimais.get(0));
                    decimaisCupom.setValorTotal(stringDecimais.get(2));
                }
            } else if (stringDecimais.size() == 3 && quantidade == 0) {
                if (stringDecimais.get(2) >= stringDecimais.get(0)) {
                    decimaisCupom.setValorUnitario(stringDecimais.get(0));
                    decimaisCupom.setValorTotal(stringDecimais.get(2));
                }
            } else if (stringDecimais.size() == 2 && quantidade > 0) {
                if (stringDecimais.get(0) >= stringDecimais.get(1)) {
                    decimaisCupom.setValorUnitario(stringDecimais.get(0));
                    decimaisCupom.setValorTotal(stringDecimais.get(0) * quantidade);
                } else if ((stringDecimais.get(1) / quantidade) == stringDecimais.get(0)) {
                    decimaisCupom.setValorUnitario(stringDecimais.get(1) / quantidade);
                    decimaisCupom.setValorTotal(stringDecimais.get(1));
                }
            } else if (stringDecimais.size() == 2 && quantidade == 0) {
                if (stringDecimais.get(0).equals(stringDecimais.get(1))) {
                    decimaisCupom.setValorUnitario(stringDecimais.get(0));
                    decimaisCupom.setValorTotal(stringDecimais.get(1));
                }
            }
            return decimaisCupom;

        } else if (stringDecimais.size() == 4) {
            Double total = multiplicaDoubleComPrecisao(stringDecimais.get(0), stringDecimais.get(1));
            if (total.equals(stringDecimais.get(3))) {
                decimaisCupom.setValorUnitario(stringDecimais.get(1));
                decimaisCupom.setValorTotal(total);
            }
        } else if (stringDecimais.size() == 3) {
            Double total = multiplicaDoubleComPrecisao(stringDecimais.get(0), stringDecimais.get(1));
            if (total.equals(stringDecimais.get(2))) {
                decimaisCupom.setValorUnitario(stringDecimais.get(1));
                decimaisCupom.setValorTotal(total);
            }
        }
        return decimaisCupom;
    }

    private boolean isUnidadeKG(String valorUnitarioETotal) {
        Matcher matcher = PATTERN_UNIDADE_KG.matcher(valorUnitarioETotal);
        return matcher.find();
    }

    private int pegaQuantidade(String texto) {
        String valor = texto.substring(texto.indexOf("\n") + 1, texto.length());
        Matcher matcher = PATTERN_QUANTIDADE.matcher(valor);

        if (matcher.find()) {
            String quantidade = normalizaLetras(matcher.group(1));
            return devolveInt(quantidade);
        }
        return 0;
    }

    private String normalizaLetras(String texto) {
        Matcher matcher = PATTERN_NORMALIZA_LETRAS.matcher(texto);

        while (matcher.find()) {
            String like = matcher.group(0);
            try {
                texto = texto.replaceAll(like, MAP_LETRA_NUMERO.get(like));
            } catch (PatternSyntaxException e) {
                e.printStackTrace();
                texto = texto.replaceAll(like, " (Erro) (" + like + ") ");
            }
        }
        return texto;
    }

    private int devolveInt(String quantidade) {
        int valor;
        try {
            valor = Integer.valueOf(quantidade);
        } catch (NumberFormatException e) {
            valor = 0;
        }
        return valor;
    }

    private Double devolveDouble(String y) {
        Double valor;
        try {
            valor = Double.valueOf(y);
        } catch (NumberFormatException e) {
            valor = Double.valueOf(0);
        }
        return valor;
    }

    private Double multiplicaDoubleComPrecisao(double x, double y) {
        Double valor = (x * y);
        try {
            valor = new BigDecimal(valor).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
        } catch (NumberFormatException e) {
            valor = Double.valueOf(0);
        }
        return valor;
    }
}
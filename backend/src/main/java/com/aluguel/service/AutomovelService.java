package com.aluguel.service;

import com.aluguel.dto.AutomovelRequest;
import com.aluguel.exception.NaoEncontradoException;
import com.aluguel.exception.NegocioException;
import com.aluguel.model.Automovel;
import com.aluguel.repository.AutomovelRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutomovelService {
    private final AutomovelRepository automoveis;

    public AutomovelService(AutomovelRepository automoveis) { this.automoveis = automoveis; }

    @Transactional(readOnly = true)
    public List<Automovel> listar() { return automoveis.findAll(); }

    @Transactional
    public Automovel criar(AutomovelRequest r) {
        if (automoveis.existsByPlaca(r.placa())) throw new NegocioException("Placa ja cadastrada.");
        if (automoveis.existsByMatricula(r.matricula())) throw new NegocioException("Matricula ja cadastrada.");
        var a = new Automovel();
        aplicar(a, r);
        return automoveis.save(a);
    }

    @Transactional
    public Automovel atualizar(Long id, AutomovelRequest r) {
        var a = automoveis.findById(id).orElseThrow(() -> new NaoEncontradoException("Automovel nao encontrado."));
        if (!a.getPlaca().equals(r.placa()) && automoveis.existsByPlaca(r.placa()))
            throw new NegocioException("Placa ja cadastrada.");
        if (!a.getMatricula().equals(r.matricula()) && automoveis.existsByMatricula(r.matricula()))
            throw new NegocioException("Matricula ja cadastrada.");
        aplicar(a, r);
        return a;
    }

    @Transactional
    public void excluir(Long id) {
        if (!automoveis.existsById(id)) throw new NaoEncontradoException("Automovel nao encontrado.");
        automoveis.deleteById(id);
    }

    private void aplicar(Automovel a, AutomovelRequest r) {
        a.setMatricula(r.matricula());
        a.setAno(r.ano());
        a.setMarca(r.marca());
        a.setModelo(r.modelo());
        a.setPlaca(r.placa());
        a.setTipoProprietario(r.tipoProprietario());
    }
}

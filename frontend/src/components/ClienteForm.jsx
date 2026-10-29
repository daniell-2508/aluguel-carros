import { useState } from 'react'

const vazio = { nome: '', rg: '', cpf: '', endereco: '', profissao: '', rendimentos: [] }

/** Formulario de dados do cliente (cadastro e edicao). Maximo de 3 rendimentos. */
export default function ClienteForm({ inicial, onSubmit, rotulo = 'Salvar' }) {
  const [d, setD] = useState({ ...vazio, ...inicial, rendimentos: inicial?.rendimentos ?? [] })
  const set = (campo) => (e) => setD({ ...d, [campo]: e.target.value })

  const setRend = (i, campo, valor) =>
    setD({ ...d, rendimentos: d.rendimentos.map((r, j) => (j === i ? { ...r, [campo]: valor } : r)) })
  const addRend = () =>
    setD({ ...d, rendimentos: [...d.rendimentos, { entidadeEmpregadora: '', valor: '' }] })
  const delRend = (i) => setD({ ...d, rendimentos: d.rendimentos.filter((_, j) => j !== i) })

  const enviar = (e) => {
    e.preventDefault()
    onSubmit({
      nome: d.nome, rg: d.rg, cpf: d.cpf, endereco: d.endereco, profissao: d.profissao,
      rendimentos: d.rendimentos.map((r) => ({ ...r, valor: Number(r.valor) })),
    })
  }

  return (
    <form onSubmit={enviar}>
      <label>Nome</label><input required value={d.nome} onChange={set('nome')} />
      <div className="linha">
        <div><label>RG</label><input required value={d.rg} onChange={set('rg')} /></div>
        <div><label>CPF</label><input required value={d.cpf} onChange={set('cpf')} /></div>
      </div>
      <label>Endereço</label><input required value={d.endereco} onChange={set('endereco')} />
      <label>Profissão</label><input value={d.profissao ?? ''} onChange={set('profissao')} />

      <label>Rendimentos (máximo 3)</label>
      {d.rendimentos.map((r, i) => (
        <div className="linha" key={i} style={{ marginBottom: 6 }}>
          <input required placeholder="Entidade empregadora" value={r.entidadeEmpregadora}
                 onChange={(e) => setRend(i, 'entidadeEmpregadora', e.target.value)} />
          <input required type="number" min="0" step="0.01" placeholder="Valor" value={r.valor}
                 onChange={(e) => setRend(i, 'valor', e.target.value)} />
          <button type="button" className="perigo" style={{ flex: '0 0 auto' }} onClick={() => delRend(i)}>×</button>
        </div>
      ))}
      <button type="button" className="sec" disabled={d.rendimentos.length >= 3} onClick={addRend}>
        + Rendimento
      </button>
      <p><button type="submit">{rotulo}</button></p>
    </form>
  )
}

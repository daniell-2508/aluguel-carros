import { useEffect, useState } from 'react'
import { api, getSessao } from '../api.js'

const vazio = { automovelId: '', dataInicio: '', dataFim: '', valorTotal: '' }
const brl = (v) => (v == null ? '-' : Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }))

export default function Pedidos() {
  const { perfil } = getSessao()
  const ehCliente = perfil === 'CLIENTE'
  const [pedidos, setPedidos] = useState([])
  const [carros, setCarros] = useState([])
  const [form, setForm] = useState(vazio)
  const [editId, setEditId] = useState(null)
  const [msg, setMsg] = useState({ tipo: '', texto: '' })
  const [aval, setAval] = useState(null)

  const carregar = () => api('/pedidos').then(setPedidos).catch((e) => setMsg({ tipo: 'erro', texto: e.message }))
  useEffect(() => { carregar(); api('/automoveis').then(setCarros).catch(() => {}) }, [])
  const set = (c) => (e) => setForm({ ...form, [c]: e.target.value })

  const executar = async (fn, ok) => {
    try { await fn(); setMsg({ tipo: 'ok', texto: ok }); carregar() } catch (e) { setMsg({ tipo: 'erro', texto: e.message }) }
  }

  const salvar = (e) => {
    e.preventDefault()
    const body = { ...form, automovelId: Number(form.automovelId), valorTotal: Number(form.valorTotal) }
    executar(async () => {
      await api(editId ? `/pedidos/${editId}` : '/pedidos', { method: editId ? 'PUT' : 'POST', body })
      setForm(vazio); setEditId(null)
    }, editId ? 'Pedido modificado.' : 'Pedido enviado para análise.')
  }

  const editar = (p) => {
    setEditId(p.id)
    setForm({ automovelId: p.automovelId, dataInicio: p.dataInicio, dataFim: p.dataFim, valorTotal: p.valorTotal })
  }

  const abrirAvaliacao = (p) =>
    setAval({ pedido: p, parecer: '', credito: false, valor: p.valorTotal, juros: '1.5' })

  const avaliar = (aprovado) => {
    const body = { aprovado, parecer: aval.parecer }
    if (aprovado && aval.credito) {
      body.valorFinanciado = Number(aval.valor)
      body.taxaJurosMensal = Number(aval.juros)
    }
    executar(async () => {
      await api(`/pedidos/${aval.pedido.id}/avaliacao`, { method: 'POST', body })
      setAval(null)
    }, aprovado ? 'Pedido aprovado.' : 'Pedido reprovado.')
  }

  return (
    <>
      <div className="card">
        <h2>{ehCliente ? 'Meus pedidos de aluguel' : 'Pedidos de aluguel'}</h2>
        {msg.texto && <div className={msg.tipo}>{msg.texto}</div>}
        <table>
          <thead><tr><th>#</th>{!ehCliente && <th>Cliente</th>}<th>Automóvel</th><th>Período</th><th>Valor</th><th>Status</th><th>Parecer</th><th /></tr></thead>
          <tbody>
            {pedidos.map((p) => (
              <tr key={p.id}>
                <td>{p.id}</td>
                {!ehCliente && <td>{p.clienteNome}</td>}
                <td>{p.automovel}</td>
                <td>{p.dataInicio} a {p.dataFim}</td>
                <td>{brl(p.valorTotal)}{p.bancoCredito && <><br /><small>Crédito {p.bancoCredito}: {brl(p.valorFinanciado)} ({p.taxaJurosMensal}% a.m.)</small></>}</td>
                <td><span className={`badge ${p.status}`}>{p.status.replace('_', ' ')}</span></td>
                <td>{p.parecer}{p.avaliador && <><br /><small>por {p.avaliador}</small></>}</td>
                <td className="acoes">
                  {p.status === 'EM_ANALISE' && <button onClick={() => editar(p)}>Modificar</button>}
                  {!ehCliente && p.status === 'EM_ANALISE' && (<>
                    <button onClick={() => abrirAvaliacao(p)}>Avaliar</button></>)}
                  {!ehCliente && p.status === 'APROVADO' && (
                    <button onClick={() => executar(() => api(`/pedidos/${p.id}/contrato`, { method: 'POST' }), 'Contrato executado.')}>Executar contrato</button>)}
                  {['EM_ANALISE', 'APROVADO'].includes(p.status) && (
                    <button className="perigo" onClick={() => confirm('Cancelar pedido?') && executar(() => api(`/pedidos/${p.id}/cancelar`, { method: 'POST' }), 'Pedido cancelado.')}>Cancelar</button>)}
                </td>
              </tr>
            ))}
            {pedidos.length === 0 && <tr><td colSpan="8">Nenhum pedido.</td></tr>}
          </tbody>
        </table>
      </div>

      {aval && (
        <div className="card">
          <h2>Avaliar pedido #{aval.pedido.id} ({aval.pedido.clienteNome})</h2>
          <label>Parecer financeiro</label>
          <textarea rows="2" value={aval.parecer} onChange={(e) => setAval({ ...aval, parecer: e.target.value })} />
          {perfil === 'BANCO' && (
            <>
              <label style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
                <input type="checkbox" style={{ width: 'auto' }} checked={aval.credito}
                       onChange={(e) => setAval({ ...aval, credito: e.target.checked })} />
                Conceder contrato de crédito
              </label>
              {aval.credito && (
                <div className="linha">
                  <div><label>Valor financiado (R$)</label>
                    <input type="number" min="0.01" step="0.01" value={aval.valor}
                           onChange={(e) => setAval({ ...aval, valor: e.target.value })} /></div>
                  <div><label>Juros mensais (%)</label>
                    <input type="number" min="0" step="0.01" value={aval.juros}
                           onChange={(e) => setAval({ ...aval, juros: e.target.value })} /></div>
                </div>
              )}
            </>
          )}
          <p className="acoes">
            <button onClick={() => avaliar(true)}>Aprovar</button>
            <button className="perigo" onClick={() => avaliar(false)}>Reprovar</button>
            <button className="sec" onClick={() => setAval(null)}>Fechar</button>
          </p>
        </div>
      )}

      {(ehCliente || editId) && (
        <div className="card">
          <h2>{editId ? `Modificar pedido #${editId}` : 'Novo pedido de aluguel'}</h2>
          <form onSubmit={salvar}>
            <label>Automóvel</label>
            <select required value={form.automovelId} onChange={set('automovelId')}>
              <option value="">Selecione...</option>
              {carros.map((a) => <option key={a.id} value={a.id}>{a.marca} {a.modelo} {a.ano} - {a.placa}</option>)}
            </select>
            <div className="linha">
              <div><label>Início</label><input required type="date" value={form.dataInicio} onChange={set('dataInicio')} /></div>
              <div><label>Fim</label><input required type="date" value={form.dataFim} onChange={set('dataFim')} /></div>
              <div><label>Valor total (R$)</label><input required type="number" min="0.01" step="0.01" value={form.valorTotal} onChange={set('valorTotal')} /></div>
            </div>
            <p className="acoes">
              <button type="submit">{editId ? 'Salvar alterações' : 'Enviar pedido'}</button>
              {editId && <button type="button" className="sec" onClick={() => { setEditId(null); setForm(vazio) }}>Cancelar edição</button>}
            </p>
          </form>
        </div>
      )}
    </>
  )
}

import { useEffect, useState } from 'react'
import { api, getSessao } from '../api.js'

const vazio = { matricula: '', ano: new Date().getFullYear(), marca: '', modelo: '', placa: '', tipoProprietario: 'EMPRESA' }

export default function Automoveis() {
  const podeEditar = getSessao().perfil !== 'CLIENTE'
  const [lista, setLista] = useState([])
  const [form, setForm] = useState(vazio)
  const [editId, setEditId] = useState(null)
  const [erro, setErro] = useState('')

  const carregar = () => api('/automoveis').then(setLista).catch((e) => setErro(e.message))
  useEffect(() => { carregar() }, [])
  const set = (c) => (e) => setForm({ ...form, [c]: e.target.value })

  const salvar = async (e) => {
    e.preventDefault()
    try {
      await api(editId ? `/automoveis/${editId}` : '/automoveis',
        { method: editId ? 'PUT' : 'POST', body: { ...form, ano: Number(form.ano) } })
      setForm(vazio); setEditId(null); setErro(''); carregar()
    } catch (err) { setErro(err.message) }
  }

  const excluir = async (id) => {
    if (!confirm('Excluir automóvel?')) return
    try { await api(`/automoveis/${id}`, { method: 'DELETE' }); carregar() } catch (err) { setErro(err.message) }
  }

  return (
    <>
      <div className="card">
        <h2>Automóveis</h2>
        {erro && <div className="erro">{erro}</div>}
        <table>
          <thead><tr><th>Marca/Modelo</th><th>Ano</th><th>Placa</th><th>Matrícula</th><th>Proprietário</th><th /></tr></thead>
          <tbody>
            {lista.map((a) => (
              <tr key={a.id}>
                <td>{a.marca} {a.modelo}</td><td>{a.ano}</td><td>{a.placa}</td><td>{a.matricula}</td>
                <td>{a.tipoProprietario}</td>
                <td className="acoes">{podeEditar && (<>
                  <button onClick={() => { setEditId(a.id); setForm(a) }}>Editar</button>
                  <button className="perigo" onClick={() => excluir(a.id)}>Excluir</button></>)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {podeEditar && (
        <div className="card">
          <h2>{editId ? 'Editar automóvel' : 'Novo automóvel'}</h2>
          <form onSubmit={salvar}>
            <div className="linha">
              <div><label>Marca</label><input required value={form.marca} onChange={set('marca')} /></div>
              <div><label>Modelo</label><input required value={form.modelo} onChange={set('modelo')} /></div>
              <div><label>Ano</label><input required type="number" value={form.ano} onChange={set('ano')} /></div>
            </div>
            <div className="linha">
              <div><label>Placa</label><input required value={form.placa} onChange={set('placa')} /></div>
              <div><label>Matrícula</label><input required value={form.matricula} onChange={set('matricula')} /></div>
              <div><label>Registrado como propriedade de</label>
                <select value={form.tipoProprietario} onChange={set('tipoProprietario')}>
                  <option value="CLIENTE">Cliente</option><option value="EMPRESA">Empresa</option><option value="BANCO">Banco</option>
                </select></div>
            </div>
            <p className="acoes">
              <button type="submit">Salvar</button>
              {editId && <button type="button" className="sec" onClick={() => { setEditId(null); setForm(vazio) }}>Cancelar</button>}
            </p>
          </form>
        </div>
      )}
    </>
  )
}

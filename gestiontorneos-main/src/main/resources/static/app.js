'use strict';
const $ = selector => document.querySelector(selector);
const state = { view: 'resumen', deportes: [], equipos: [], torneos: [], selected: null, inscriptions: [], search: '', filter: '' };
const labels = { INSCRIPCION: 'Inscripciones abiertas', EN_CURSO: 'En curso', FINALIZADO: 'Finalizado' };
const singular = { deportes: 'deporte', equipos: 'equipo', torneos: 'torneo' };
const escape = value => String(value ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const sportName = id => state.deportes.find(item => item.id === id)?.nombre || 'Deporte no disponible';
const teamName = id => state.equipos.find(item => item.id === id)?.nombre || `Equipo ${id}`;
let editing = null;
let busy = false;
async function api(path, method = 'GET', body) {
  const response = await fetch(new URL(path, new URL('./', location.href)), { method, headers: body ? {'Content-Type':'application/json'} : {}, body: body ? JSON.stringify(body) : undefined });
  if (!response.ok) {
    const problem = await response.json().catch(() => ({}));
    throw new Error(problem.detail || 'No se pudo completar la operación. Intenta nuevamente.');
  }
  return response.status === 204 ? null : response.json();
}
function notice(message = '', error = false) { $('#notice').textContent = message; $('#notice').className = error ? 'error' : ''; }
async function refresh() {
  $('#content').setAttribute('aria-busy', 'true');
  $('#refresh').disabled = true;
  try {
    const [deportes, equipos, torneos] = await Promise.all(['deportes','equipos','torneos'].map(path => api(path)));
    Object.assign(state, {deportes, equipos, torneos});
    if (state.selected) {
      state.selected = torneos.find(item => item.id === state.selected.id) || null;
      state.inscriptions = state.selected ? await api(`torneos/${state.selected.id}/inscripciones`) : [];
    }
    render();
    return true;
  } catch (error) { notice('No se pudieron actualizar los datos. ' + error.message, true); return false; }
  finally { $('#content').setAttribute('aria-busy', 'false'); $('#refresh').disabled = false; }
}
const button = (action, text, id = '', cls = 'secondary') => `<button class="${cls}" data-action="${action}" data-id="${id}">${text}</button>`;
const empty = text => `<div class="empty"><strong>Aquí comienza la competición</strong>${text}</div>`;
function tournamentCard(t) {
  return `<article class="card"><div class="card-top"><span class="sport">${escape(sportName(t.deporteId))}</span><span class="badge ${t.estado}">${labels[t.estado]}</span></div><h2>${escape(t.nombre)}</h2><div class="meta"><span>${escape(t.fechaInicio)}</span><span>${t.cupoEquipos} equipos máx.</span></div><div class="actions">${button('detail','Ver torneo →',t.id)}</div></article>`;
}
function render() {
  document.querySelectorAll('[data-view]').forEach(item => { item.classList.toggle('selected', item.dataset.view === state.view); item.setAttribute('aria-current', item.dataset.view === state.view ? 'page' : 'false'); });
  if (state.selected) return renderDetail();
  if (state.view === 'resumen') {
    $('#content').innerHTML = `<div class="hero"><div class="eyebrow">EL DEPORTE NOS ENCUENTRA</div><h1>Tu próxima competición<br>empieza aquí.</h1><p>Organiza los torneos, reúne a tus equipos y lleva cada inscripción en un solo lugar.</p>${button('new-tournament','+ Crear torneo','','')}</div><div class="stats"><div class="stat">Torneos<strong>${state.torneos.length}</strong><span>${state.torneos.filter(t=>t.estado==='EN_CURSO').length} en curso</span></div><div class="stat">Equipos<strong>${state.equipos.length}</strong><span>Listos para competir</span></div><div class="stat">Deportes<strong>${state.deportes.length}</strong><span>Más formas de encontrarnos</span></div></div><div class="heading"><h2>Tu calendario de torneos</h2>${button('all-tournaments','Ver todos →')}</div><div class="cards">${[...state.torneos].sort((a,b)=>a.fechaInicio.localeCompare(b.fechaInicio)).slice(0,3).map(tournamentCard).join('') || empty('Crea tu primer deporte y equipo, y organiza un torneo.')}</div>`;
    return;
  }
  const type = state.view;
  $('#content').innerHTML = `<div class="heading"><div><h1>${type[0].toUpperCase()+type.slice(1)}</h1><div class="muted">${{torneos:'Dale espacio a la próxima gran competición.',equipos:'Los protagonistas de cada encuentro.',deportes:'Define las reglas para formar tus equipos.'}[type]}</div></div>${button('create','+ Crear '+singular[type],'','')}</div><div class="toolbar"><input id="search" type="search" aria-label="Buscar por nombre" placeholder="Buscar por nombre…" value="${escape(state.search)}">${type==='torneos'?`<select id="filter" aria-label="Filtrar por estado"><option value="">Todos los estados</option>${Object.entries(labels).map(([v,l])=>`<option value="${v}" ${v===state.filter?'selected':''}>${l}</option>`).join('')}</select>`:''}</div><div id="results"></div>`;
  renderResults();
}
function renderResults() {
  const type = state.view;
  const items = state[type].filter(item => item.nombre.toLocaleLowerCase().includes(state.search.toLocaleLowerCase()) && (!state.filter || type!=='torneos' || item.estado===state.filter));
  if (!items.length) { $('#results').innerHTML = empty('No hay registros que mostrar. Puedes crear uno o cambiar la búsqueda.'); return; }
  if (type === 'torneos') { $('#results').innerHTML = `<div class="cards">${items.map(tournamentCard).join('')}</div>`; return; }
  $('#results').innerHTML = `<div class="table-wrap"><table><thead><tr><th>Nombre</th><th>${type==='deportes'?'Mínimo de jugadores':'Deporte'}</th><th>${type==='deportes'?'Máximo de jugadores':'Jugadores'}</th><th>Acciones</th></tr></thead><tbody>${items.map(item=>`<tr><td><strong>${escape(item.nombre)}</strong></td><td>${type==='deportes'?item.minJugadores:escape(sportName(item.deporteId))}</td><td>${type==='deportes'?item.maxJugadores:item.cantidadJugadores}</td><td><div class="actions">${button('edit','Editar',item.id)}${button('delete','Eliminar',item.id,'danger')}</div></td></tr>`).join('')}</tbody></table></div>`;
}
function renderDetail() {
  const t = state.selected, open = t.estado === 'INSCRIPCION';
  const eligible = state.equipos.filter(e=>e.deporteId===t.deporteId && !state.inscriptions.some(i=>i.equipoId===e.id));
  $('#content').innerHTML = `<div class="heading">${button('back','← Volver a torneos')}</div><div class="detail"><span class="badge ${t.estado}">${labels[t.estado]}</span><h1 style="margin-top:18px">${escape(t.nombre)}</h1><p class="muted">${escape(sportName(t.deporteId))} · ${escape(t.fechaInicio)} · ${state.inscriptions.length} de ${t.cupoEquipos} equipos inscritos</p><div class="actions">${open?button('edit','Editar torneo',t.id)+button('start','Iniciar torneo',t.id)+button('delete','Eliminar torneo',t.id,'danger'):t.estado==='EN_CURSO'?button('finish','Finalizar torneo',t.id):''}</div></div><div class="heading"><h2>Equipos inscritos</h2></div>${open && state.inscriptions.length<t.cupoEquipos?`<div class="toolbar"><select id="team" aria-label="Equipo a inscribir"><option value="">Selecciona un equipo del mismo deporte</option>${eligible.map(e=>`<option value="${e.id}">${escape(e.nombre)}</option>`).join('')}</select>${button('register','+ Inscribir equipo','','')}</div>${!eligible.length?'<p class="muted">No hay equipos disponibles del mismo deporte. Crea uno en la sección Equipos.</p>':''}`:''}<div class="cards">${state.inscriptions.map(i=>`<article class="card"><h2>${escape(teamName(i.equipoId))}</h2><div class="muted">Inscripción confirmada</div>${open?button('unregister','Cancelar inscripción',i.equipoId,'danger'):''}</article>`).join('')||empty('Este torneo aún no tiene equipos inscritos.')}</div>`;
}
function openForm(type, item = null) {
  if(type !== 'deportes' && !state.deportes.length) { notice('Primero crea un deporte en la sección Deportes.',true); return; }
  editing = {type, id:item?.id};
  $('#form-title').textContent = `${item?'Editar':'Crear'} ${singular[type]}`;
  const input = (name,label,kind='number',min=1,max='') => `<label>${label}<input name="${name}" type="${kind}" required ${kind==='number'?`min="${min}" step="1" ${max?`max="${max}"`:''}`:''} ${kind==='text'?'maxlength="100"':''} value="${escape(item?.[name] ?? '')}"></label>`;
  $('#fields').innerHTML = input('nombre','Nombre','text') + (type==='deportes'?input('minJugadores','Mínimo de jugadores')+input('maxJugadores','Máximo de jugadores'):`<label>Deporte<select name="deporteId" required><option value="">Selecciona un deporte</option>${state.deportes.map(d=>`<option value="${d.id}" ${d.id===item?.deporteId?'selected':''}>${escape(d.nombre)}</option>`).join('')}</select></label>`+(type==='equipos'?input('cantidadJugadores','Cantidad de jugadores'):input('cupoEquipos','Cupo de equipos','number',2,128)+input('fechaInicio','Fecha de inicio','date')));
  $('#form-error').textContent = '';
  $('#editor').showModal();
}
async function mutate(path, method, body, message) {
  if (busy) return;
  busy = true;
  const controls = [...document.querySelectorAll('button')]; controls.forEach(b=>b.disabled=true);
  try { await api(path,method,body); notice(message); await refresh(); }
  catch(error) { notice(error.message,true); }
  finally { busy=false; controls.forEach(b=>b.disabled=false); }
}
$('#form').addEventListener('submit',async event=>{
  event.preventDefault(); if(busy) return;
  const body = Object.fromEntries(new FormData(event.target));
  for(const key of Object.keys(body)) if(!['nombre','fechaInicio'].includes(key)) body[key]=Number(body[key]);
  body.nombre=body.nombre.trim();
  if(!body.nombre || (editing.type==='deportes' && body.minJugadores>body.maxJugadores)){ $('#form-error').textContent='Revisa el nombre y el rango de jugadores: el mínimo no puede superar al máximo.'; return; }
  busy=true; $('#save').disabled=true; $('#close').disabled=true; $('#cancel').disabled=true;
  try { await api(editing.type+(editing.id?'/'+editing.id:''),editing.id?'PUT':'POST',body); $('#editor').close(); notice('Cambios guardados correctamente.'); await refresh(); }
  catch(error){ $('#form-error').textContent=error.message; }
  finally {busy=false; $('#save').disabled=false; $('#close').disabled=false; $('#cancel').disabled=false;}
});
$('#editor').addEventListener('cancel',event=>{if(busy)event.preventDefault();});
$('#close').onclick=$('#cancel').onclick=()=>$('#editor').close();
$('#refresh').onclick=()=>{notice();refresh();};
document.querySelector('nav').addEventListener('click',event=>{const nav=event.target.closest('[data-view]');if(!nav||busy)return;Object.assign(state,{view:nav.dataset.view,selected:null,search:'',filter:''});notice();render();});
$('#content').addEventListener('input',event=>{if(event.target.id==='search'){state.search=event.target.value;renderResults();}});
$('#content').addEventListener('change',event=>{if(event.target.id==='filter'){state.filter=event.target.value;renderResults();}});
$('#content').addEventListener('click',async event=>{
  const target=event.target.closest('[data-action]'); if(!target||busy)return;
  const action=target.dataset.action,id=Number(target.dataset.id),type=state.selected?'torneos':state.view;
  if(action==='new-tournament')return openForm('torneos');
  if(action==='all-tournaments'||action==='back'){Object.assign(state,{view:'torneos',selected:null,search:'',filter:''});return render();}
  if(action==='create')return openForm(type);
  if(action==='edit')return openForm(type,state[type].find(item=>item.id===id));
  if(action==='detail'){
    busy=true;
    try{const inscriptions=await api(`torneos/${id}/inscripciones`);Object.assign(state,{selected:state.torneos.find(t=>t.id===id),inscriptions,view:'torneos'});render();}
    catch(error){notice(error.message,true);}finally{busy=false;}return;
  }
  if(action==='delete' && confirm('¿Eliminar este registro? Esta acción no se puede deshacer.'))return mutate(`${type}/${id}`,'DELETE',null,'Registro eliminado.');
  if(action==='register'){const team=Number($('#team').value);if(!team)return notice('Selecciona un equipo para inscribir.',true);return mutate(`torneos/${state.selected.id}/inscripciones`,'POST',{equipoId:team},'Equipo inscrito correctamente.');}
  if(action==='unregister' && confirm('¿Cancelar la inscripción de este equipo?'))return mutate(`torneos/${state.selected.id}/inscripciones/${id}`,'DELETE',null,'Inscripción cancelada.');
  if(['start','finish'].includes(action) && confirm(action==='start'?'¿Iniciar el torneo? Se cerrarán las inscripciones.':'¿Finalizar el torneo? No podrás reabrirlo.'))return mutate(`torneos/${id}/${action==='start'?'inicio':'finalizacion'}`,'POST',null,'Estado del torneo actualizado.');
});
refresh();

'use strict';

const $ = (id) => document.getElementById(id);
const categories = {
  GROCERIES: { label: 'Mercado', icon: 'bag', color: '#8a72df', bg: '#f0ebfb', ink: '#654eab' },
  PHARMA: { label: 'Saúde e farmácia', icon: 'health', color: '#76ad9e', bg: '#eaf4ef', ink: '#3c7766' },
  AUTO: { label: 'Transporte', icon: 'car', color: '#719bc9', bg: '#edf3fa', ink: '#476f99' },
  FOOD: { label: 'Alimentação', icon: 'food', color: '#d6a55e', bg: '#fbf1e4', ink: '#916326' },
  LEISURE: { label: 'Lazer', icon: 'sun', color: '#ca87a3', bg: '#f8edf2', ink: '#a05a77' },
  OTHER: { label: 'Outros', icon: 'other', color: '#9697ab', bg: '#f0f0f5', ink: '#64667c' }
};
const brl = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
const decimal = new Intl.NumberFormat('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
const dateFormat = new Intl.DateTimeFormat('pt-BR', { day: '2-digit', month: 'short', year: 'numeric', timeZone: 'UTC' });
const today = new Intl.DateTimeFormat('en-CA', { timeZone: 'America/Sao_Paulo', year: 'numeric', month: '2-digit', day: '2-digit' }).format(new Date());
const state = { from: '', to: '', category: '', expenses: [], summary: null, loaded: false, savedId: null, request: 0, controller: null };
let toastTimer;

function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, (char) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[char]);
}
function icon(name, extra = '') { return `<svg class="icon ${extra}" aria-hidden="true"><use href="#i-${name}"/></svg>`; }
function categoryInfo(key) { return categories[key] || categories.OTHER; }
function categoryStyle(key) {
  const category = categoryInfo(key);
  return `--category-color:${category.color};--category-bg:${category.bg};--category-ink:${category.ink}`;
}
function parseDate(value) { return new Date(`${value}T12:00:00Z`); }
function setMonth(value) {
  if (!/^\d{4}-\d{2}$/.test(value)) return;
  const [year, month] = value.split('-').map(Number);
  state.from = `${value}-01`;
  state.to = `${value}-${String(new Date(Date.UTC(year, month, 0)).getUTCDate()).padStart(2, '0')}`;
  $('month').value = value;
  updatePeriod();
}
function updatePeriod() {
  $('from').value = state.from;
  $('to').value = state.to;
  const short = (date) => new Intl.DateTimeFormat('pt-BR', { day: '2-digit', month: 'short', timeZone: 'UTC' }).format(parseDate(date));
  $('period-caption').textContent = `${short(state.from)} — ${short(state.to)}`;
}
function shiftMonth(delta) {
  const [year, month] = state.from.split('-').map(Number);
  const shifted = new Date(Date.UTC(year, month - 1 + delta, 1));
  if (shifted.getUTCFullYear() < 1900 || shifted.getUTCFullYear() > 9998) return;
  setMonth(`${shifted.getUTCFullYear()}-${String(shifted.getUTCMonth() + 1).padStart(2, '0')}`);
  loadBudget();
}
async function requestJson(url, options = {}) {
  const response = await fetch(url, options);
  const body = await response.json().catch(() => null);
  if (!response.ok) throw new Error(body?.detail || `Não foi possível concluir a solicitação (HTTP ${response.status}).`);
  if (body === null) throw new Error('O servidor retornou uma resposta inesperada. Tente atualizar a página.');
  return body;
}
async function loadBudget() {
  const request = ++state.request;
  state.controller?.abort();
  const controller = new AbortController();
  state.controller = controller;
  const timeout = setTimeout(() => controller.abort(), 15000);
  $('overview').setAttribute('aria-busy', 'true');
  $('overview').classList.toggle('first-load', !state.loaded);
  $('expense-list').setAttribute('aria-busy', 'true');
  $('load-error').hidden = true;
  $('export-csv').disabled = true;
  const params = new URLSearchParams({ from: state.from, to: state.to });
  const listParams = new URLSearchParams(params);
  if (state.category) listParams.set('category', state.category);
  try {
    const [summary, expenses] = await Promise.all([
      requestJson(`/api/expenses/summary?${params}`, { signal: controller.signal }),
      requestJson(`/api/expenses?${listParams}`, { signal: controller.signal })
    ]);
    if (request !== state.request) return;
    state.summary = summary;
    state.expenses = expenses;
    state.loaded = true;
    renderSummary();
    renderExpenses();
    $('connection-status').innerHTML = '<span class="status-dot"></span>Salvo neste computador';
  } catch (error) {
    if (request !== state.request) return;
    $('load-error-detail').textContent = error.name === 'AbortError' ? 'A consulta demorou mais que o esperado. Confira se a aplicação está aberta e tente novamente.' : error instanceof TypeError ? 'Não foi possível conectar à aplicação. Confira se ela está aberta e tente novamente.' : error.message;
    $('load-error').hidden = false;
    $('connection-status').innerHTML = '<span class="status-dot offline"></span>Sem conexão com os dados';
    if (!state.loaded) {
      $('total-amount').textContent = '—';
      $('total-context').textContent = 'Os dados não foram carregados.';
      $('category-chart').replaceChildren();
      $('category-list').replaceChildren();
      $('expense-list').innerHTML = '<div class="empty-state"><h3>Vamos tentar de novo?</h3><p>Seu orçamento aparece aqui quando a conexão com a aplicação voltar.</p></div>';
      $('list-caption').textContent = 'Não foi possível carregar a lista.';
    } else {
      $('list-caption').textContent = 'Dados anteriores. A atualização não foi concluída.';
      $('total-context').textContent = 'Dados da última consulta bem-sucedida.';
    }
  } finally {
    clearTimeout(timeout);
    if (request === state.request) {
      $('overview').setAttribute('aria-busy', 'false');
      $('expense-list').setAttribute('aria-busy', 'false');
    }
  }
}
function renderSummary() {
  const summary = state.summary;
  $('total-amount').innerHTML = `<span class="currency">R$</span>${decimal.format(summary.total)}`;
  $('total-amount').classList.toggle('long-amount', summary.total >= 1000000);
  $('total-context').textContent = summary.count === 0 ? 'Ainda sem despesas neste período.' : `em ${summary.count} ${summary.count === 1 ? 'despesa registrada' : 'despesas registradas'} no período`;
  $('average-amount').textContent = brl.format(summary.count ? summary.total / summary.count : 0);
  const groups = [...summary.categories].sort((a, b) => b.total - a.total);
  $('top-category').textContent = groups.length ? categoryInfo(groups[0].category).label : '—';
  $('clear-category').hidden = !state.category;
  $('category-chart').innerHTML = groups.map((group) => `<span class="chart-segment" style="${categoryStyle(group.category)};flex-grow:${Math.max(0, Number(group.total))}"></span>`).join('');
  $('category-list').innerHTML = groups.length ? groups.map((group) => {
    const category = categoryInfo(group.category);
    const percent = summary.total ? Math.round(group.total / summary.total * 100) : 0;
    return `<button class="category-item" type="button" data-category="${escapeHtml(group.category)}" style="${categoryStyle(group.category)}" aria-pressed="${state.category === group.category}" aria-label="Filtrar ${category.label}, ${brl.format(group.total)}, ${percent}% dos gastos"><span class="category-dot"></span><span class="category-item-copy"><strong>${category.label}</strong><span class="category-values"><b>${brl.format(group.total)}</b><span>· ${percent}%</span></span></span></button>`;
  }).join('') : '<p class="category-empty">As categorias aparecem assim que você registra sua primeira despesa.</p>';
  $('category-footnote').hidden = !groups.length;
}
function filteredExpenses() {
  const query = $('search').value.trim().toLocaleLowerCase('pt-BR');
  return state.expenses.filter((expense) => expense.description.toLocaleLowerCase('pt-BR').includes(query));
}
function renderExpenses() {
  const expenses = filteredExpenses();
  $('visible-count').textContent = expenses.length;
  $('export-csv').disabled = !expenses.length;
  const header = '<div class="table-head" aria-hidden="true"><span>Descrição</span><span>Categoria</span><span>Data</span><span>Valor</span></div>';
  if (!expenses.length) {
    const filtered = !!state.category || !!$('search').value.trim();
    $('expense-list').innerHTML = `${header}<div class="empty-state"><span class="empty-symbol">${icon(filtered ? 'search' : 'wallet')}</span><h3>${filtered ? 'Nenhuma despesa por aqui' : 'Seu mês começa com um registro'}</h3><p>${filtered ? 'Tente outra busca ou remova os filtros para ver mais despesas.' : 'Cadastre seu primeiro gasto e acompanhe seu orçamento de perto.'}</p><button class="button secondary" type="button" data-empty-action="${filtered ? 'clear' : 'create'}">${filtered ? 'Limpar filtros' : 'Cadastrar primeira despesa'}</button></div>`;
  } else {
    $('expense-list').innerHTML = header + `<div role="list" aria-label="Despesas do período">${expenses.map((expense) => {
      const category = categoryInfo(expense.category);
      const isDemo = /^DEMONSTRAÇÃO\s*[—-]\s*/i.test(expense.description);
      const description = isDemo ? expense.description.replace(/^DEMONSTRAÇÃO\s*[—-]\s*/i, '') : expense.description;
      return `<article role="listitem" class="expense-row ${state.savedId === expense.id ? 'saved-row' : ''}" style="${categoryStyle(expense.category)}"><div class="expense-description"><span class="expense-icon">${icon(category.icon)}</span><div><div class="expense-name">${escapeHtml(description)}</div><span class="mobile-category">${category.label}</span>${isDemo ? '<span class="demo-label">Dado de demonstração</span>' : ''}</div></div><span class="category-chip">${category.label}</span><time class="expense-date" datetime="${escapeHtml(expense.occurredOn)}">${escapeHtml(dateFormat.format(parseDate(expense.occurredOn)).replaceAll(' de ', ' '))}</time><span class="expense-amount">${brl.format(expense.amount)}</span></article>`;
    }).join('')}</div>`;
  }
  const limit = state.expenses.length === 200 ? ' · limite de 200 registros; a busca considera esta lista' : '';
  $('list-caption').textContent = `${expenses.length} ${expenses.length === 1 ? 'despesa na lista' : 'despesas na lista'}${limit}`;
}
function selectCategory(category) {
  state.category = category;
  $('list-category').value = category;
  loadBudget();
}
function showEditor() {
  $('expense-editor').hidden = false;
  $('new-expense').setAttribute('aria-expanded', 'true');
  $('occurredOn').max = today;
  if (!$('occurredOn').value) $('occurredOn').value = today;
  $('description').focus({ preventScroll: true });
  $('expense-editor').scrollIntoView({ block: 'nearest', behavior: 'smooth' });
}
function hideEditor() {
  $('expense-editor').hidden = true;
  $('new-expense').setAttribute('aria-expanded', 'false');
  $('new-expense').focus({ preventScroll: true });
}
function showToast(message) {
  clearTimeout(toastTimer);
  $('toast-message').textContent = message;
  $('toast').hidden = false;
  toastTimer = setTimeout(() => { $('toast').hidden = true; }, 6000);
}
$('expense-form').addEventListener('submit', async (event) => {
  event.preventDefault();
  const raw = $('amount').value.trim();
  $('form-error').hidden = true;
  if (!/^\d+(?:[.,]\d{1,2})?$/.test(raw)) {
    $('form-error').textContent = 'Informe um valor como 35,90, sem separador de milhar e com até duas casas decimais.';
    $('form-error').hidden = false;
    $('amount').focus();
    return;
  }
  const amount = Number(raw.replace(',', '.'));
  if (amount <= 0 || amount > 999999999.99) {
    $('form-error').textContent = 'O valor precisa ser maior que zero e no máximo R$ 999.999.999,99.';
    $('form-error').hidden = false;
    $('amount').focus();
    return;
  }
  if (!$('description').value.trim()) {
    $('form-error').textContent = 'Escreva uma descrição para identificar a despesa.';
    $('form-error').hidden = false;
    $('description').focus();
    return;
  }
  const payload = { description: $('description').value.trim(), amount, category: $('category').value, occurredOn: $('occurredOn').value };
  $('save-expense').disabled = true;
  $('save-expense').textContent = 'Salvando…';
  $('cancel-editor').disabled = true;
  $('close-editor').disabled = true;
  try {
    const saved = await requestJson('/api/expenses', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload), signal: AbortSignal.timeout(20000) });
    state.savedId = saved.id;
    $('expense-form').reset();
    $('occurredOn').value = today;
    hideEditor();
    $('search').value = '';
    state.category = '';
    $('list-category').value = '';
    setMonth(saved.occurredOn.slice(0, 7));
    showToast('Despesa salva. Seu orçamento foi atualizado.');
    await loadBudget();
    if (!$('load-error').hidden) showToast('Despesa salva. Não foi possível atualizar a consulta; clique em Tentar novamente.');
  } catch (error) {
    $('form-error').textContent = error.name === 'TimeoutError' || error instanceof TypeError ? 'Não foi possível confirmar o salvamento. Consulte a lista antes de tentar novamente para evitar duplicatas.' : error.message;
    $('form-error').hidden = false;
  } finally {
    $('save-expense').disabled = false;
    $('save-expense').innerHTML = `Salvar despesa${icon('arrow')}`;
    $('cancel-editor').disabled = false;
    $('close-editor').disabled = false;
  }
});
$('new-expense').addEventListener('click', showEditor);
$('close-editor').addEventListener('click', hideEditor);
$('cancel-editor').addEventListener('click', hideEditor);
$('previous-month').addEventListener('click', () => shiftMonth(-1));
$('next-month').addEventListener('click', () => shiftMonth(1));
$('month').addEventListener('change', () => { if ($('month').value) { setMonth($('month').value); loadBudget(); } });
$('retry').addEventListener('click', loadBudget);
$('search').addEventListener('input', () => { if (state.loaded) renderExpenses(); });
$('list-category').addEventListener('change', (event) => selectCategory(event.target.value));
$('clear-category').addEventListener('click', () => selectCategory(''));
$('category-list').addEventListener('click', (event) => {
  const button = event.target.closest('[data-category]');
  if (button) selectCategory(state.category === button.dataset.category ? '' : button.dataset.category);
});
$('expense-list').addEventListener('click', (event) => {
  const button = event.target.closest('[data-empty-action]');
  if (!button) return;
  if (button.dataset.emptyAction === 'create') showEditor();
  else { $('search').value = ''; selectCategory(''); }
});
$('period-form').addEventListener('submit', (event) => {
  event.preventDefault();
  const from = $('from').value;
  const to = $('to').value;
  const days = (parseDate(to) - parseDate(from)) / 86400000;
  if (days < 0 || days > 365 || !Number.isFinite(days)) {
    $('period-error').textContent = 'O início deve ser anterior ao fim. Escolha até 366 dias.';
    $('period-error').hidden = false;
    return;
  }
  $('period-error').hidden = true;
  state.from = from;
  state.to = to;
  $('month').value = '';
  updatePeriod();
  $('custom-period').open = false;
  loadBudget();
});
$('export-csv').addEventListener('click', () => {
  const cell = (value) => {
    let text = String(value);
    if (/^[=+\-@\t\r]/.test(text)) text = `'${text}`;
    return `"${text.replaceAll('"', '""')}"`;
  };
  const rows = [['Descrição', 'Categoria', 'Data', 'Valor (R$)'], ...filteredExpenses().map((expense) => [expense.description, categoryInfo(expense.category).label, expense.occurredOn, Number(expense.amount).toFixed(2).replace('.', ',')])];
  const blob = new Blob(['\uFEFF' + rows.map((row) => row.map(cell).join(';')).join('\r\n')], { type: 'text/csv;charset=utf-8' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = `budgetai-${state.from}-${state.to}.csv`;
  link.click();
  setTimeout(() => URL.revokeObjectURL(url), 1000);
  showToast('Lista exportada para CSV.');
});
document.querySelectorAll('.nav-link').forEach((link) => link.addEventListener('click', () => {
  document.querySelectorAll('.nav-link').forEach((item) => { item.classList.remove('active'); item.removeAttribute('aria-current'); });
  link.classList.add('active');
  link.setAttribute('aria-current', 'page');
}));
document.addEventListener('click', (event) => { if (!$('custom-period').contains(event.target)) $('custom-period').open = false; });
document.addEventListener('keydown', (event) => {
  if (event.key === 'Escape') {
    $('custom-period').open = false;
    if (!$('expense-editor').hidden && !$('save-expense').disabled) hideEditor();
  }
});
setMonth(today.slice(0, 7));
$('occurredOn').value = today;
$('occurredOn').max = today;
loadBudget();

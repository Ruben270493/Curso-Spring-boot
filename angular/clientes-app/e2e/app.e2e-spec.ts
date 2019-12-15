import { ClientesAppPage } from './app.po';

describe('clientes-app App', function() {
  let page: ClientesAppPage;

  beforeEach(() => {
    page = new ClientesAppPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});

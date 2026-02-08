import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProgrammeLayoutComponent } from './programme-layout.component';

describe('ProgrammeLayoutComponent', () => {
  let component: ProgrammeLayoutComponent;
  let fixture: ComponentFixture<ProgrammeLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProgrammeLayoutComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProgrammeLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

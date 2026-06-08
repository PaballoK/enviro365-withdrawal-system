import { Component } from '@angular/core';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
@Component({
  selector: 'app-loader',
  standalone: true,
  imports: [ProgressSpinnerModule],
  templateUrl: './loader.component.html',
  styleUrl: './loader.component.css'
})
export class LoaderComponent {

}
